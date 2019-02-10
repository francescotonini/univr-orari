package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import java.util.List;

import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.repositories.OfficesRepository;

public class OfficesViewModel extends BaseViewModel {
    /**
     * Initializes a new instance of this class
     *
     * @param application
     */
    public OfficesViewModel(@NonNull Application application, OfficesRepository officesRepository) {
        super(application);

        repository = officesRepository;
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Office}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Office>>> getOffices() {
        return repository.getOffices();
    }

    /**
     * Sets the offices passed as favorites
     * @param offices offices to set as favorites
     */
    public void setOffices(List<Office> offices) {
        // TODO: map selected offices directly into a string without a for loop
        String officeIds = "";
        for(Office office: offices) {
            officeIds += office.getId() + "-";
        }

        PreferenceHelper.setString(PreferenceHelper.Keys.ROOMS_OFFICES, officeIds);
        PreferenceHelper.setBoolean(PreferenceHelper.Keys.ROOMS_DID_FIRST_START, true);
    }

    /**
     * This class Factory
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        /**
         * Initializes a Factory for this viewmodel
         *
         * @param application instance of this application
         * @param officesRepository instance of {@link OfficesRepository}
         */
        public Factory(Application application, OfficesRepository officesRepository) {
            this.application = application;
            this.officesRepository = officesRepository;
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
            return (T) new OfficesViewModel(application, officesRepository);
        }

        private final Application application;
        private final OfficesRepository officesRepository;
    }

    private final OfficesRepository repository;
}
