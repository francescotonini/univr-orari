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
import android.util.ArrayMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import it.francescotonini.univrorari.helpers.PreferenceHelper;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Course;
import it.francescotonini.univrorari.models.Lesson;
import it.francescotonini.univrorari.models.Teaching;

/**
 * Handles data from API and DB about timetables
 */
public class LessonsRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api instance of {@link UniVRApi}
     */
    public LessonsRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);

        getPreferences();

        lessonsMap = new ArrayMap<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Lesson}
     * @param month month of the timetable to retrieve
     * @param year year of the timetable to retrieve
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Lesson>>> getLessons(final int month, final int year) {
        if (!lessonsMap.containsKey(calculateKey(month, year))) {
            lessonsMap.put(calculateKey(month, year), new MutableLiveData<>());

            Logger.i(LessonsRepository.class.getSimpleName(), "Cache NOT found for key " + calculateKey(month, year));
            loadLessons(month, year);
        }
        else {
            Logger.i(LessonsRepository.class.getSimpleName(), "Cache found for key " + calculateKey(month, year));
        }

        return lessonsMap.get(calculateKey(month, year));
    }

    private void loadLessons(final int month, final int year) {
        if (selectedCourse == null || selectedTeachings == null) {
            Logger.w(LessonsRepository.class.getSimpleName(), "Can't get lessons since course and teachings are null");

            return;
        }

        Logger.i(LessonsRepository.class.getSimpleName(), "Loading lessons for key " + calculateKey(month, year));

        List<String> yearIds = new ArrayList<>();
        for (Teaching teaching : selectedTeachings) {
            if (!yearIds.contains(teaching.getYearId())) {
                yearIds.add(teaching.getYearId());
            }
        }

        Observable<String> observable = Observable.fromIterable(yearIds);
        observable
        .flatMap((Function<String, ObservableSource<List<Lesson>>>) s -> getApi().getLessons(selectedCourse.getAcademicYearId(), selectedCourse.getId(), s, month, year))
        .retry(3)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Lesson>>() {
            List<Lesson> lessons = new ArrayList<>();

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Lesson> l) {
                Logger.i(LessonsRepository.class.getSimpleName(), "Got " + lessons.size() + " lessons for key " + calculateKey(month, year));

                lessons.addAll(l);
            }

            @Override
            public void onError(Throwable e) {
                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get lessons: " + e.getMessage());
                lessonsMap.get(calculateKey(month, year)).setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }

            @Override
            public void onComplete() {
                List<Lesson> finalLessons = new ArrayList<>();
                for (Lesson lesson : lessons) {
                    for (Teaching teaching : selectedTeachings) {
                        if (lesson.getId().hashCode() == teaching.getId().hashCode()) {
                            finalLessons.add(lesson);
                        }
                    }
                }

                lessonsMap.get(calculateKey(month, year)).setValue(new ApiResponse<>(finalLessons));
            }
        });
    }

    /**
     * Removes every observable connected before
     */
    public void clear() {
        lessonsMap.clear();
        getPreferences();
    }

    private String calculateKey(int month, int year) {
        return year + "-" + month;
    }

    private void getPreferences() {
        selectedCourse = new Gson().fromJson(PreferenceHelper.getString(PreferenceHelper.Keys.COURSE), Course.class);
        selectedTeachings = new Gson().fromJson(PreferenceHelper.getString(PreferenceHelper.Keys.TEACHINGS), new TypeToken<List<Teaching>>(){}.getType());
    }

    private Course selectedCourse;
    private List<Teaching> selectedTeachings;
    private Map<String, MutableLiveData<ApiResponse<List<Lesson>>>> lessonsMap;
}
