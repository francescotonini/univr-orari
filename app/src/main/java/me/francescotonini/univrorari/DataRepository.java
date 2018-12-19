package me.francescotonini.univrorari;

import android.content.Context;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.repositories.CoursesRepository;
import me.francescotonini.univrorari.repositories.LessonsRepository;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Wraps every repository of this project
 */
public class DataRepository {
    /**
     * Gets an instance of this class
     * @param appContext application context
     * @return an instance of {@link DataRepository}
     */
    public static DataRepository getInstance(final Context appContext, final AppExecutors appExecutors) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(appContext, appExecutors);
                }
            }
        }

        return instance;
    }

    private DataRepository(final Context context, final AppExecutors appExecutors) {
        uniVRApi = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UniVRApi.class);
        lessonsRepository = new LessonsRepository(appExecutors, uniVRApi);
        coursesRepository = new CoursesRepository(appExecutors, uniVRApi);
    }

    /**
     * Gets an instance of {@link LessonsRepository}
     * @return an instance of {@link LessonsRepository}
     */
    public LessonsRepository getLessonsRepository() {
        return lessonsRepository;
    }

    /**
     * Gets an instance of {@link CoursesRepository}
     * @return an instance of {@link CoursesRepository}
     */
    public CoursesRepository getCoursesRepository() {
        return coursesRepository;
    }

    private static DataRepository instance;
    private final UniVRApi uniVRApi;
    private final LessonsRepository lessonsRepository;
    private final CoursesRepository coursesRepository;
}