package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.ArrayMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
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

        this.lessonsRepository = lessonsRepository;
        lessonColors = new ArrayMap<>();
    }

    /**
     * Gets an observable of a list of {@link Lesson}
     * @param month month of the timetable to retrieve
     * @param year year of the timetable to retrieve
     * @return if the observed value is NULL then something went wrong, otherwise the value is a list
     */
    public LiveData<List<Lesson>> getLessons(final int month, final int year) {
        return lessonsRepository.getLessons(month, year);
    }

    /**
     * Gets a lesson if it meets the above criteria
     * @param startTime start time of the lesson
     * @param endTime end time of the lesson
     * @return if the criteria are met, returns an instance of {@link Lesson}; otherwise NULL
     */
    public Lesson getLesson(final Calendar startTime, final Calendar endTime) {
        Lesson lesson = null;

        int year = startTime.get(Calendar.YEAR);
        int month = startTime.get(Calendar.MONTH) + 1;

        List<Lesson> lessons = lessonsRepository.getLessons(month, year).getValue();
        if (lessons == null || lessons.size() == 0) {
            Logger.e(LessonsViewModel.class.getSimpleName(), "No lessons found");
            return lesson;
        }

        for (Lesson l: lessons) {
            Calendar lessonStartTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            lessonStartTime.setTimeInMillis(l.getStartTimestamp());
            Calendar lessonEndTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            lessonEndTime.setTimeInMillis(l.getStartTimestamp());

            if (lessonStartTime.equals(startTime) && lessonEndTime.equals(lessonEndTime)) {
                lesson = l;
            }
        }

        return lesson;
    }

    /**
     * Gets a color that represents a lesson
     * @param id lesson id
     * @return an int representing a color
     */
    public int getLessonColor(String id)
    {
        if (!lessonColors.containsKey(id)) {
            lessonColors.put(id, getApplication().getResources().getIntArray(R.array.cell_colors)[lessonColors.size()]);
        }

        return lessonColors.get(id);
    }

    /**
     * Removes every observable connected before
     */
    public void clear() {
        lessonsRepository.clear();
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
         * @param <T>        type of the viewmodel
         * @return the viewmodel
         */
        @Override public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new LessonsViewModel(application, lessonsRepository);
        }

        private final Application application;
        private final LessonsRepository lessonsRepository;
    }

    private final LessonsRepository lessonsRepository;
    private final Map<String, Integer> lessonColors;
}

