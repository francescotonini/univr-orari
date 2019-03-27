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
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import it.francescotonini.univrorari.AppExecutors;
import it.francescotonini.univrorari.Logger;
import it.francescotonini.univrorari.api.ApiError;
import it.francescotonini.univrorari.api.UniVRApi;
import it.francescotonini.univrorari.helpers.LoggerHelper;
import it.francescotonini.univrorari.helpers.PreferenceHelper;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Office;
import it.francescotonini.univrorari.models.Room;
import retrofit2.HttpException;

/**
 * Handles communication between data and view model
 */
public class RoomsRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     */
    public RoomsRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);
        this.rooms = new MutableLiveData<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Room}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Room>>> getRooms() {
        loadRooms();

        return rooms;
    }

    /**
     * Reloads the data
     */
    public void reload() {
        if (rooms.getValue() != null && rooms.getValue().isSuccessful()) {
            rooms.getValue().getData().clear();
        }

        loadRooms();
    }

    private void loadRooms() {
        Logger.i(RoomsRepository.class.getSimpleName(), "Loading rooms");

        List<Office> offices = new Gson().fromJson(PreferenceHelper.getString(PreferenceHelper.Keys.OFFICES), new TypeToken<List<Office>>(){}.getType());

        if (offices == null || offices.size() == 0) {
            Logger.w(RoomsRepository.class.getSimpleName(), "Unable to load rooms since no offices are available");
            return;
        }

        Observable<Office> observable = Observable.fromIterable(offices);
        observable
        .flatMap((Function<Office, ObservableSource<List<Room>>>) s -> getApi().getRooms(s.getId())
            .map(t -> {
                for (Room r : t) {
                    r.setOfficeName(s.getName());
                }

                return t;
            }))
        .retry(3)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Room>>() {
            List<Room> result = new ArrayList<>();

            @Override
            public void onSubscribe(Disposable d) {
                Logger.v(RoomsRepository.class.getSimpleName(), "getRooms request subscribed");
            }

            @Override
            public void onNext(List<Room> r) {
                Logger.i(RoomsRepository.class.getSimpleName(), "Got " + r.size());

                result.addAll(r);
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
                    LoggerHelper.getInstance().logJsonParseError(RoomsRepository.class.getSimpleName(), e.getMessage());
                }
                else {
                    LoggerHelper.getInstance().logUnknownError(RoomsRepository.class.getSimpleName(), e.getMessage());
                }

                Logger.e(RoomsRepository.class.getSimpleName(), "Unable to get lessons: " + e.getMessage());
                rooms.setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }

            @Override
            public void onComplete() {
                rooms.setValue(new ApiResponse<>(result));

                Logger.v(RoomsRepository.class.getSimpleName(), "getRooms request completed");
            }
        });
    }

    private final MutableLiveData<ApiResponse<List<Room>>> rooms;
}
