package me.francescotonini.univrorari;

import android.app.Application;
import android.content.ContextWrapper;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Main class. Let's you access to singletons
 */
public class UniVROrariApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // This is a class with different thread pools
        appExecutors = new AppExecutors();

        // Build preferences
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
     * Gets an instance of {@link AppExecutors}
     * @return an instance of {@link AppExecutors}
     */
    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    private AppExecutors appExecutors;
}
