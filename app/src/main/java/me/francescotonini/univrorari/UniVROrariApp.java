package me.francescotonini.univrorari;

import android.app.Application;
import android.content.ContextWrapper;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Android Application class. Used for accessing singletons.
 */
public class UniVROrariApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        appExecutors = new AppExecutors();

        // Builds preferences
        new Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(BuildConfig.APPLICATION_ID)
            .setUseDefaultSharedPreference(true)
            .build();
    }

    /**
     * Gets an instance of {@link DataRepository}
     * @return an instance of {@link DataRepository}
     */
    public DataRepository getDataRepository() {
        return DataRepository.getInstance(getApplicationContext(), appExecutors);
    }

    /**
     * Get an instance of {@link AppExecutors}
     * @return an instance of {@link AppExecutors}
     */
    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    private AppExecutors appExecutors;
}
