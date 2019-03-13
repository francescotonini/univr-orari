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

package it.francescotonini.univrorari.viewmodels;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import java.util.List;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Course;
import it.francescotonini.univrorari.models.Teaching;
import it.francescotonini.univrorari.repositories.CoursesRepository;

/**
 * Represents the logic behind an activity that handles mediator
 */
public class CoursesViewModel extends BaseViewModel {
    /**
     * Initializes a new instance of this class
     *
     * @param application
     */
    public CoursesViewModel(@NonNull Application application, CoursesRepository coursesRepository) {
        super(application);

        repository = coursesRepository;
    }

    /**
     * Gets an observable of a list of {@link Course}
     * @return if the observed value is NULL then something went wrong, otherwise the value is a list
     */
    public LiveData<ApiResponse<List<Course>>> getCourses() {
        return repository.getCourses();
    }

    /**
     * Gets an observable of a list of {@link Teaching}
     * @param academicYearId academic year id
     * @param courseId course id
     * @return if the observed value is NULL then something went wrong, otherwise the value is a list
     */
    public LiveData<ApiResponse<List<Teaching>>> getTeachings(String academicYearId, String courseId) {
        return repository.getTeachings(academicYearId, courseId);
    }

    /**
     * Saves the course the user has choosen from the configuration page
     * @param selectedCourse course selected
     * @param teachings a list of teachings
     */
    public void savePreferences(Course selectedCourse, List<Teaching> teachings) {
        repository.savePreferences(selectedCourse, teachings);
    }

    /**
     * This class Factory
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        /**
         * Initializes a Factory for this viewmodel
         *
         * @param application application
         */
        public Factory(@NonNull Application application, CoursesRepository coursesRepository) {
            this.application = application;
            this.coursesRepository = coursesRepository;
        }

        /**
         * Gets the actual viewmodel
         *
         * @param modelClass model of the... Viewmodel
         * @param <T> type of the viewmodel
         * @return the viewmodel
         */
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new CoursesViewModel(application, coursesRepository);
        }

        @NonNull
        private final Application application;
        private final CoursesRepository coursesRepository;
    }

    private final CoursesRepository repository;
}

