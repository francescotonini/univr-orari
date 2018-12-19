package me.francescotonini.univrorari.repositories;

import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.api.UniVRApi;

/**
 * Base repository
 */
public abstract class BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api instance of {@link UniVRApi}
     */
    public BaseRepository(AppExecutors appExecutors, UniVRApi api) {
        this.appExecutors = appExecutors;
        this.api = api;
    }

    /**
     * Gets an instance of {@link AppExecutors}
     * @return an instance of {@link AppExecutors}
     */
    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    /**
     * Gets an instance of {@link UniVRApi}
     * @return an instance of {@link UniVRApi}
     */
    public UniVRApi getApi() {
        return api;
    }

    private final UniVRApi api;
    private final AppExecutors appExecutors;
}

