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
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.util.ArrayMap;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import it.francescotonini.univrorari.Logger;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.api.ApiError;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Lesson;
import it.francescotonini.univrorari.repositories.LessonsRepository;
import it.francescotonini.univrorari.views.MainActivity;

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
    public LessonsViewModel(Application application, LessonsRepository lessonsRepository, ApiResponse.ApiResponseListener listener) {
        super(application);

        this.repository = lessonsRepository;
        this.colors = new ArrayMap<>();
        this.repository.setListener(listener);
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Lesson}
     * @param startDate start date
     * @param endDate endDate
     * @return a list of {@link Lesson} ready to be shown
     */
    public List<WeekViewDisplayable<Lesson>> getLessons(final Calendar startDate, final Calendar endDate) {
        List<Lesson> lessons = repository.getLessons(startDate, endDate);
        List<WeekViewDisplayable<Lesson>> finalLessons = new ArrayList<>();

        if (lessons != null && lessons.size() != 0) {
            for (Lesson lesson : lessons) {
                if (lesson.getName() == null || lesson.getRoom() == null) {
                    Logger.e(LessonsViewModel.class.getSimpleName(), "Ignoring lesson because name or room is NULL");
                    continue;
                }

                WeekViewEvent<Lesson> event = lesson.toWeekViewEvent();
                event.setColor(getLessonColor(lesson.getName()));
                finalLessons.add(event);
            }
        }

        return finalLessons;
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
         * @param listener an instance of {@link it.francescotonini.univrorari.models.ApiResponse.ApiResponseListener}
         */
        public Factory(Application application, LessonsRepository lessonsRepository, ApiResponse.ApiResponseListener listener) {
            this.application = application;
            this.lessonsRepository = lessonsRepository;
            this.listener = listener;
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
            return (T) new LessonsViewModel(application, lessonsRepository, listener);
        }

        private final Application application;
        private final LessonsRepository lessonsRepository;
        private final ApiResponse.ApiResponseListener listener;
    }

    private int getLessonColor(String id)
    {
        if (!colors.containsKey(id)) {
            colors.put(id, getApplication().getResources().getIntArray(R.array.cell_colors)[colors.size() % 8]);
        }

        return colors.get(id);
    }

    private final LessonsRepository repository;
    private final Map<String, Integer> colors;
}

