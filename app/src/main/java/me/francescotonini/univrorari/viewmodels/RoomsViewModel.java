package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import java.util.List;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Room;
import me.francescotonini.univrorari.repositories.OfficesRepository;
import me.francescotonini.univrorari.repositories.RoomsRepository;

public class RoomsViewModel extends BaseViewModel {
    /**
     * Initializes a new instance of this class
     *
     * @param application
     */
    public RoomsViewModel(@NonNull Application application, RoomsRepository roomsRepository) {
        super(application);

        repository = roomsRepository;
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Room}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Room>>> getRooms() {
        return repository.getRooms();
    }

    /**
     * Refreshes the data
     */
    public void refresh() {
        repository.reload();
    }

    /**
     * This class Factory
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        /**
         * Initializes a Factory for this viewmodel
         *
         * @param application instance of this application
         * @param roomsRepository instance of {@link OfficesRepository}
         */
        public Factory(Application application, RoomsRepository roomsRepository) {
            this.application = application;
            this.roomsRepository = roomsRepository;
        }

        /**
         * Gets the actual viewmodel
         *
         * @param modelClass model of the... Viewmodel
         * @param <T> type of the viewmodel
         * @return the viewmodel
         */
        @Override public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new RoomsViewModel(application, roomsRepository);
        }

        private final Application application;
        private final RoomsRepository roomsRepository;
    }

    private RoomsRepository repository;
}
