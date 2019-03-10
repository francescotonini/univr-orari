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

package me.francescotonini.univrorari.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.api.ApiError;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Teaching;

/**
 * Handles data from API and DB about courses
 */
public class CoursesRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api instance of {@link UniVRApi}
     */
    public CoursesRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);
    }

    /**
     * Saves the course the user has choosen from the configuration page
     * @param selectedCourse course selected
     * @param teachings a list of teachings
     */
    public void savePreferences(Course selectedCourse, List<Teaching> teachings) {
        PreferenceHelper.setString(PreferenceHelper.Keys.COURSE, new Gson().toJson(selectedCourse));
        PreferenceHelper.setString(PreferenceHelper.Keys.TEACHINGS, new Gson().toJson(teachings));
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Course}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Course>>> getCourses() {
        if (courses == null) {
            courses = new MutableLiveData<>();
        }

        getApi().getCourses()
        .retry(3)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Course>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Course> c) {
                Logger.i(CoursesRepository.class.getSimpleName(), String.format("Got %s courses", c.size()));
                courses.setValue(new ApiResponse<>(c));
            }

            @Override
            public void onError(Throwable e) {
                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get lessons: " + e.getMessage());
                courses.setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }

            @Override
            public void onComplete() {
                Logger.v(CoursesRepository.class.getSimpleName(), "getCourses request completed");
            }
        });

        return courses;
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Teaching}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Teaching>>> getTeachings(String academicYearId, String courseId) {
        if (teachings == null) {
            teachings = new MutableLiveData<>();
        }

        getApi().getTeachings(academicYearId, courseId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Teaching>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Teaching> t) {
                Logger.i(CoursesRepository.class.getSimpleName(), String.format("Got %s teachings", t.size()));
                teachings.setValue(new ApiResponse<>(t));
            }

            @Override
            public void onError(Throwable e) {
                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get teachings: " + e.getMessage());
                teachings.setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }

            @Override
            public void onComplete() {
                Logger.v(CoursesRepository.class.getSimpleName(), "getTeachings request completed");
            }
        });

        return teachings;
    }

    private MutableLiveData<ApiResponse<List<Course>>> courses;
    private MutableLiveData<ApiResponse<List<Teaching>>> teachings;
}
