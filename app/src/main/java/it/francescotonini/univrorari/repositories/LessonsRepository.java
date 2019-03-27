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

import android.util.ArrayMap;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Calendar;
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
import it.francescotonini.univrorari.helpers.CacheHelper;
import it.francescotonini.univrorari.helpers.LoggerHelper;
import it.francescotonini.univrorari.helpers.PreferenceHelper;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Course;
import it.francescotonini.univrorari.models.Lesson;
import it.francescotonini.univrorari.models.Teaching;
import retrofit2.HttpException;

/**
 * Handles data from API and DB about timetables
 */
public class LessonsRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api          instance of {@link UniVRApi}
     */
    public LessonsRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);

        this.lessons = new ArrayMap<>();
    }

    /**
     * Sets the listener for this repository
     * @param listener listener for this repository
     */
    public void setListener(ApiResponse.ApiResponseListener listener) {
        this.listener = listener;
    }

    /**
     * Gets a list of {@link Lesson} from the start date to the end date
     * @param startDate start date
     * @param endDate   end date
     * @return a list of {@link Lesson}
     */
    public List<Lesson> getLessons(final Calendar startDate, final Calendar endDate) {
        // Get cache if available
        if (!lessons.containsKey(new Pair<>(startDate, endDate))) {
            lessons.put(new Pair<>(startDate, endDate), new Gson().fromJson(CacheHelper.get(calculateKey(startDate, endDate), "[ ]"), new TypeToken<List<Lesson>>() { }.getType()));
            loadLessons(startDate, endDate);
        }

        return lessons.get(new Pair<>(startDate, endDate));
    }

    private Course getCourse() {
        return new Gson().fromJson(PreferenceHelper.getString(PreferenceHelper.Keys.COURSE), Course.class);
    }

    private List<Teaching> getSelectedTeachings() {
        return new Gson().fromJson(PreferenceHelper.getString(PreferenceHelper.Keys.TEACHINGS), new TypeToken<List<Teaching>>() { }.getType());
    }

    private void loadLessons(Calendar startDate, Calendar endDate) {
        final int month = startDate.get(Calendar.MONTH);
        final int year = startDate.get(Calendar.YEAR);

        final List<Teaching> selectedTeachings = getSelectedTeachings();
        final Course course = getCourse();
        if (course == null || selectedTeachings == null) {
            Logger.w(LessonsRepository.class.getSimpleName(), "Can't get lessons since course and teachings are null");
            return;
        }

        Logger.i(LessonsRepository.class.getSimpleName(), "Loading lessons for month " + month + " and year " + year);

        List<String> yearIds = new ArrayList<>();
        for (Teaching teaching : selectedTeachings) {
            if (!yearIds.contains(teaching.getYearId())) {
                yearIds.add(teaching.getYearId());
            }
        }

        Observable<String> observable = Observable.fromIterable(yearIds);
        observable
        .flatMap((Function<String, ObservableSource<List<Lesson>>>) s -> getApi().getLessons(course.getAcademicYearId(), course.getId(), s, month, year))
        .retry(3)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Lesson>>() {
            List<Lesson> totalLessons = new ArrayList<>();

            @Override
            public void onSubscribe(Disposable d) {
                Logger.v(CoursesRepository.class.getSimpleName(), "getLessons request completed");
            }

            @Override
            public void onNext(List<Lesson> lessons) {
                totalLessons.addAll(lessons);
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
                    LoggerHelper.getInstance().logJsonParseError(LessonsRepository.class.getSimpleName(), e.getMessage());
                }
                else {
                    LoggerHelper.getInstance().logUnknownError(LessonsRepository.class.getSimpleName(), e.getMessage());
                }

                Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get totalLessons: " + e.getMessage());

                if (listener != null) {
                    listener.onError(ApiError.BAD_RESPONSE);
                }
            }

            @Override
            public void onComplete() {
                List<Lesson> finalLessons = new ArrayList<>();
                for (Lesson lesson : totalLessons) {
                    for (Teaching teaching : selectedTeachings) {
                        if (lesson.getId() != null && teaching.getId() != null &&
                            lesson.getId().hashCode() == teaching.getId().hashCode()) {
                            finalLessons.add(lesson);
                        }
                    }
                }

                CacheHelper.set(calculateKey(startDate, endDate), new Gson().toJson(finalLessons));
                lessons.put(new Pair<>(startDate, endDate), finalLessons);

                if (listener != null) {
                    listener.onResponse();
                }

                Logger.v(CoursesRepository.class.getSimpleName(), "getLessons request completed");
            }
        });
    }

    /**
     * Removes lessons
     */
    public void clear() {
        lessons.clear();
    }

    private String calculateKey(Calendar startDate, Calendar endDate) {
        return startDate.get(Calendar.MONTH) + "-" + startDate.get(Calendar.MINUTE) + ":" + endDate.get(Calendar.MONTH) + "-" + endDate.get(Calendar.MINUTE);
    }

    private ApiResponse.ApiResponseListener listener;
    private final Map<Pair<Calendar, Calendar>, List<Lesson>> lessons;
}
