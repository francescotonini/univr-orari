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

package me.francescotonini.univrorari.api;

import java.util.List;
import io.reactivex.Observable;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.models.Room;
import me.francescotonini.univrorari.models.Teaching;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Defines the interface between the app and the API
 */
public interface UniVRApi {
    @GET("courses")
    Observable<List<Course>> getCourses();

    @GET("academicyear/{academicYearId}/course/{courseId}/year/{courseYearId}/lessons")
    Observable<List<Lesson>> getLessons(@Path("academicYearId") String academicYearId, @Path("courseId") String courseId,
                                  @Path("courseYearId") String courseYearId, @Query("month") int month,
                                  @Query("year") int year);

    @GET("academicyear/{academicYearId}/course/{courseId}/teachings")
    Observable<List<Teaching>> getTeachings(@Path("academicYearId") String academicYearId, @Path("courseId") String courseId);

    @GET("offices")
    Observable<List<Office>> getOffices();

    @GET("offices/{id}/rooms")
    Observable<List<Room>> getRooms(@Path("id") String id);
}
