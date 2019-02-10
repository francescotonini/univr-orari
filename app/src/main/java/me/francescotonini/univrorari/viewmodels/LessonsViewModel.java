package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.ArrayMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.repositories.LessonsRepository;

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
            colors.put(id, getApplication().getResources().getIntArray(R.array.cell_colors)[colors.size()]);
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

