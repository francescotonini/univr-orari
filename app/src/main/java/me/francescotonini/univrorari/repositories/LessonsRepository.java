package me.francescotonini.univrorari.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.ArrayMap;
import java.util.List;
import java.util.Map;
import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.api.ApiError;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Lesson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles data from API and DB about timetables
 */
public class LessonsRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api instance of {@link UniVRApi}
     */
    public LessonsRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);

        lessonsMap = new ArrayMap<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Lesson}
     * @param month month of the timetable to retrieve
     * @param year year of the timetable to retrieve
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Lesson>>> getLessons(final int month, final int year) {
        if (!lessonsMap.containsKey(calculateKey(month, year))) {
            lessonsMap.put(calculateKey(month, year), new MutableLiveData<>());

            Logger.i(LessonsRepository.class.getSimpleName(), "Cache NOT found for key " + calculateKey(month, year));
            loadLessons(month, year);
        }
        else {
            Logger.i(LessonsRepository.class.getSimpleName(), "Cache found for key " + calculateKey(month, year));
        }

        return lessonsMap.get(calculateKey(month, year));
    }

    private void loadLessons(final int month, final int year) {
        Logger.i(LessonsRepository.class.getSimpleName(), "Loading lessons for key " + calculateKey(month, year));

        getAppExecutors().networkIO().execute(() -> getApi().getLessons(PreferenceHelper.getString(PreferenceHelper.Keys.TIMETABLE_ACADEMIC_YEAR),
                PreferenceHelper.getString(PreferenceHelper.Keys.TIMETABLE_COURSE),
                PreferenceHelper.getString(PreferenceHelper.Keys.TIMETABLE_COURSE_YEAR), month, year).enqueue(new Callback<List<Lesson>>() {

            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (!response.isSuccessful()) {
                    Logger.e(LessonsRepository.class.getSimpleName(), String.format("Unable to get lessons for key %s because error code is %s", calculateKey(month, year), response.code()));
                    lessonsMap.get(calculateKey(month, year)).setValue(new ApiResponse<>(ApiError.BAD_RESPONSE));
                    return;
                }

                Logger.i(LessonsRepository.class.getSimpleName(), "Got " + response.body().size() + " lessons for key " + calculateKey(month, year));
                lessonsMap.get(calculateKey(month, year)).setValue(new ApiResponse<>(response.body()));
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get lessons: " + t.getMessage());
                lessonsMap.get(calculateKey(month, year)).setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }
        }));
    }

    /**
     * Removes every observable connected before
     */
    public void clear() {
        lessonsMap.clear();
    }

    private String calculateKey(int month, int year) {
        return year + "-" + month;
    }

    private Map<String, MutableLiveData<ApiResponse<List<Lesson>>>> lessonsMap;
}
