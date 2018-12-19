package me.francescotonini.univrorari.repositories;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import java.util.List;
import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.models.Course;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles data from API and DB about courses
 */
public class CoursesRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api          instance of {@link UniVRApi}
     */
    public CoursesRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);
    }

    /**
     * Gets an observable of a list of {@link Course}
     * @return if the observed value is NULL then something went wrong, otherwise the value is a list
     */
    public LiveData<List<Course>> getCourses() {
        if (courses == null) {
            courses = new MutableLiveData<>();
        }

        getAppExecutors().networkIO().execute(() -> getApi().getCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                if (!response.isSuccessful()) {
                    Logger.e(CoursesRepository.class.getSimpleName(), String.format("Unable to get courses because error code is %s ", response.code()));
                    courses.setValue(null);
                    return;
                }

                Logger.i(CoursesRepository.class.getSimpleName(), String.format("Got %s courses", response.body().size()));
                courses.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get lessons: " + t.getMessage());
                courses.setValue(null);
            }
        }));

        return courses;
    }

    /**
     * Saves the course the user has choosen from the configuration page
     * @param academicYearId academic year of the course selected
     * @param yearId year of the course selected
     * @param courseId unique id of the course selected
     */
    public void setCourse(String academicYearId, String yearId, String courseId) {
        Logger.d(CoursesRepository.class.getSimpleName(), String.format("(academicYearId,yearId,courseId) = (%s,%s,%s)", academicYearId, yearId, courseId));

        PreferenceHelper.setString(PreferenceHelper.Keys.ACADEMIC_YEAR, academicYearId);
        PreferenceHelper.setString(PreferenceHelper.Keys.COURSE_YEAR, yearId);
        PreferenceHelper.setString(PreferenceHelper.Keys.COURSE, courseId);
        PreferenceHelper.setBoolean(PreferenceHelper.Keys.DID_FIRST_BOOT, true);
    }

    private MutableLiveData<List<Course>> courses;
}
