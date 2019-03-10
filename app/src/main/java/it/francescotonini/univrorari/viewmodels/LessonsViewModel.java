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
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.ArrayMap;
import java.util.List;
import java.util.Map;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Lesson;
import it.francescotonini.univrorari.repositories.LessonsRepository;

/**
 * Represents the logic behind an activity that handles lessons
 */
public class LessonsViewModel extends BaseViewModel {
    /**
     * Initializes a new instance of this class
     *
     * @param application instance of this application
     * @param lessonsRepository an instance of {@link LessonsRepository}
     */
    public LessonsViewModel(Application application, LessonsRepository lessonsRepository) {
        super(application);

        repository = lessonsRepository;
        colors = new ArrayMap<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Lesson}
     * @param month month of the timetable to retrieve
     * @param year year of the timetable to retrieve
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Lesson>>> getLessons(final int month, final int year) {
        return repository.getLessons(month, year);
    }

    /**
     * Gets a color that represents a lesson
     * @param id lesson id
     * @return an int representing a color
     */
    public int getLessonColor(String id)
    {
        if (!colors.containsKey(id)) {
            colors.put(id, getApplication().getResources().getIntArray(R.array.cell_colors)[colors.size() % 8]);
        }

        return colors.get(id);
    }

    /**
     * Removes every observable connected before
     */
    public void clear() {
        repository.clear();
    }

    /**
     * This class Factory
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        /**
         * Initializes a Factory for this viewmodel
         *
         * @param application instance of this application
         * @param lessonsRepository instance of {@link LessonsRepository}
         */
        public Factory(Application application, LessonsRepository lessonsRepository) {
            this.application = application;
            this.lessonsRepository = lessonsRepository;
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
            return (T) new LessonsViewModel(application, lessonsRepository);
        }

        private final Application application;
        private final LessonsRepository lessonsRepository;
    }

    private final LessonsRepository repository;
    private final Map<String, Integer> colors;
}

