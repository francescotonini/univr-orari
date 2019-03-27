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

package it.francescotonini.univrorari.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.francescotonini.univrorari.AppExecutors;
import it.francescotonini.univrorari.Logger;
import it.francescotonini.univrorari.api.ApiError;
import it.francescotonini.univrorari.api.UniVRApi;
import it.francescotonini.univrorari.helpers.LoggerHelper;
import it.francescotonini.univrorari.helpers.PreferenceHelper;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Office;
import retrofit2.HttpException;

/**
 * Handles communication between data and view model
 */
public class OfficesRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api instance of {@link UniVRApi}
     */
    public OfficesRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);

        this.offices = new MutableLiveData<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Office}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Office>>> getOffices() {
        loadOffices();
        return offices;
    }

    /**
     * Save favorite offices
     * @param offices offices to set as favorites
     */
    public void savePreferences(List<Office> offices) {
        PreferenceHelper.setString(PreferenceHelper.Keys.OFFICES, new Gson().toJson(offices));
        PreferenceHelper.setBoolean(PreferenceHelper.Keys.DID_SELECT_OFFICES, true);
    }

    private void loadOffices() {
        getApi().getOffices()
        .retry(3)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Office>>() {

            @Override
            public void onSubscribe(Disposable d) {
                Logger.v(OfficesRepository.class.getSimpleName(), "getOffices request completed");
            }

            @Override
            public void onNext(List<Office> o) {
                Logger.i(OfficesRepository.class.getSimpleName(), "Got " + o.size());

                offices.setValue(new ApiResponse<>(o));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException)e;
                    int statusCode = httpException.code();
                    String url = httpException.response().raw().request().url().url().toString();
                    String reason = httpException.message();
                    String body = httpException.response().errorBody().toString();

                    LoggerHelper.getInstance().logNetworkError(url, reason, statusCode, body);
                }
                else if (e instanceof JsonSyntaxException || e instanceof JsonParseException) {
                    LoggerHelper.getInstance().logJsonParseError(OfficesRepository.class.getSimpleName(), e.getMessage());
                }
                else {
                    LoggerHelper.getInstance().logUnknownError(OfficesRepository.class.getSimpleName(), e.getMessage());
                }

                Logger.e(Office.class.getSimpleName(), "Unable to get lessons: " + e.getMessage());
                offices.setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }

            @Override
            public void onComplete() {
                Logger.v(OfficesRepository.class.getSimpleName(), "getOffices request completed");
            }
        });
    }

    private final MutableLiveData<ApiResponse<List<Office>>> offices;
}
