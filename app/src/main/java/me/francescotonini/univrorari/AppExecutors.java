package me.francescotonini.univrorari;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * https://github.com/googlesamples/android-architecture-components/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/AppExecutors.java
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {
    /**
     * Initialize a new instance of this class
     */
    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor());
    }

    /**
     * Gets the Disk IO
     * @return disk IO
     */
    public Executor diskIO() {
        return mDiskIO;
    }

    /**
     * Gets the Network IO
     * @return network IO
     */
    public Executor networkIO() {
        return mNetworkIO;
    }

    /**
     * Gets the main thread
     * @return main thread
     */
    public Executor mainThread() {
        return mMainThread;
    }

    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
