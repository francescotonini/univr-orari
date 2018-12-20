package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import me.francescotonini.univrorari.helpers.SingleLiveEvent;

/**
 * View model skeleton
 */
public abstract class BaseViewModel extends AndroidViewModel {
    /**
     * Represents possible states (e.g. exceptions) that the view should react to
     */
    public enum ViewModelEvent {
        NETWORK_ERROR
    }

    /**
     * Initializes a new instance of this class
     * @param application
     */
    public BaseViewModel(@NonNull Application application) {
        super(application);

        event = new SingleLiveEvent<>();
    }

    /**
     * Gets an instance of a LiveData with a {@link ViewModelEvent} that the activity should react to
     * @return an instance of a LiveData with a {@link ViewModelEvent} that the activity should react to
     */
    public LiveData<ViewModelEvent> getEvent() {
        return event;
    }

    protected void setEvent(ViewModelEvent event) {
        this.event.setValue(event);
    }

    private final SingleLiveEvent<ViewModelEvent> event;
}
