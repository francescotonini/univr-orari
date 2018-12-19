package me.francescotonini.univrorari.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.ArrayMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
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
     * @param api          instance of {@link UniVRApi}
     */
    public LessonsRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);

        lessonsMap = new ArrayMap<>();
    }

    /**
     * Gets an observable of a list of {@link Lesson}
     * @param month month of the timetable to retrieve
     * @param year year of the timetable to retrieve
     * @return if the observed value is NULL then something went wrong, otherwise the value is a list
     */
    public LiveData<List<Lesson>> getLessons(final int month, final int year) {
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

        getAppExecutors().networkIO().execute(() -> getApi().getLessons(PreferenceHelper.getString(PreferenceHelper.Keys.ACADEMIC_YEAR),
                PreferenceHelper.getString(PreferenceHelper.Keys.COURSE),
                PreferenceHelper.getString(PreferenceHelper.Keys.COURSE_YEAR), month, year).enqueue(new Callback<List<Lesson>>() {

            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (!response.isSuccessful()) {
                    Logger.e(LessonsRepository.class.getSimpleName(), String.format("Unable to get lessons for key %s because error code is %s", calculateKey(month, year), response.code()));
                    // TODO: send error
                    // lessonsMap.get(calculateKey(month, year)).setValue(null);
                    return;
                }

                Logger.i(LessonsRepository.class.getSimpleName(), "Got " + response.body().size() + " lessons for key " + calculateKey(month, year));
                ((MutableLiveData<List<Lesson>>)lessonsMap.get(calculateKey(month, year))).setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get lessons: " + t.getMessage());
                // TODO: send error
                // lessonsMap.get(calculateKey(month, year)).setValue(null);
            }
        }));
    }

    /**
     * Removes every observable connected before
     * TODO: that's not true actually
     */
    public void clear() {
        lessonsMap.clear();
    }

    private String calculateKey(int month, int year) {
        return year + "-" + month;
    }

    private long getStartOfTheMonthTimestamp(int month, int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long time = calendar.getTimeInMillis();
        return time;
    }

    private long getEndOfTheMonthTimestamp(int month, int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        long time = calendar.getTimeInMillis();
        return time;
    }

    private Map<String, LiveData<List<Lesson>>> lessonsMap;
}
