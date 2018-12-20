package me.francescotonini.univrorari.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import java.util.List;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.repositories.CoursesRepository;

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
        mediator = new MediatorLiveData<>();

        // Why do I use a mediator? The repository sends a list of courses.
        // The mediator intercepts that request and send a ViewModelEvent if something went wrong, otherwise
        // sends the list
        mediator.addSource(repository.getCourses(), courses -> {
            if (courses == null) {
                setEvent(ViewModelEvent.NETWORK_ERROR);
            }
            else {
                mediator.setValue(courses);
            }
        });
    }

    /**
     * Gets an observable of a list of {@link Course}
     * @return if the observed value is NULL then something went wrong, otherwise the value is a list
     */
    public LiveData<List<Course>> getCourses() {
        return mediator;
    }

    /**
     * Saves the course the user has choosen from the configuration page
     * @param academicYearId academic year of the course selected
     * @param yearId year of the course selected
     * @param courseId unique id of the course selected
     */
    public void setCourse(String academicYearId, String yearId, String courseId) {
        repository.setCourse(academicYearId, yearId, courseId);
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

    private final MediatorLiveData<List<Course>> mediator;
    private final CoursesRepository repository;
}

