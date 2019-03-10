/*
 * The MIT License
 *
 * Copyright (c) 2017-2019 Francesco Tonini - francescotonini.me
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
     * Save favorite offices
     * @param offices offices to set as favorites
     */
    public void savePreferences(List<Office> offices) {
        repository.savePreferences(offices);

        // TODO: this is not a good place to place the next line of code
        PreferenceHelper.setBoolean(PreferenceHelper.Keys.DID_FIRST_BOOT, true);
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
