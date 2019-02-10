package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

/**
 * View model skeleton
 */
public abstract class BaseViewModel extends AndroidViewModel {
    /**
     * Initializes a new instance of this class
     * @param application
     */
    public BaseViewModel(@NonNull Application application) {
        super(application);
    }
}
