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

package it.francescotonini.univrorari;

import android.content.Context;
import it.francescotonini.univrorari.api.UniVRApi;
import it.francescotonini.univrorari.repositories.CoursesRepository;
import it.francescotonini.univrorari.repositories.LessonsRepository;
import it.francescotonini.univrorari.repositories.OfficesRepository;
import it.francescotonini.univrorari.repositories.RoomsRepository;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Wraps every repository of this project
 */
public class DataRepository {
    /**
     * Gets an instance of this class
     * @param appContext application context
     * @return an instance of {@link DataRepository}
     */
    public static DataRepository getInstance(final Context appContext, final AppExecutors appExecutors) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(appContext, appExecutors);
                }
            }
        }

        return instance;
    }

    private DataRepository(final Context context, final AppExecutors appExecutors) {
        uniVRApi = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(UniVRApi.class);
        lessonsRepository = new LessonsRepository(appExecutors, uniVRApi);
        coursesRepository = new CoursesRepository(appExecutors, uniVRApi);
        officesRepository = new OfficesRepository(appExecutors, uniVRApi);
        roomsRepository = new RoomsRepository(appExecutors, uniVRApi);
    }

    /**
     * Gets an instance of {@link LessonsRepository}
     * @return an instance of {@link LessonsRepository}
     */
    public LessonsRepository getLessonsRepository() {
        return lessonsRepository;
    }

    /**
     * Gets an instance of {@link CoursesRepository}
     * @return an instance of {@link CoursesRepository}
     */
    public CoursesRepository getCoursesRepository() {
        return coursesRepository;
    }

    /**
     * Gets an instance of {@link OfficesRepository}
     * @return an instance of {@link OfficesRepository}
     */
    public OfficesRepository getOfficesRepository() {
        return officesRepository;
    }

    /**
     * Gets an instance of {@link RoomsRepository}
     * @return an instance of {@link RoomsRepository}
     */
    public RoomsRepository getRoomsRepository() {
        return roomsRepository;
    }

    private static DataRepository instance;
    private final UniVRApi uniVRApi;
    private final LessonsRepository lessonsRepository;
    private final CoursesRepository coursesRepository;
    private final OfficesRepository officesRepository;
    private final RoomsRepository roomsRepository;
}