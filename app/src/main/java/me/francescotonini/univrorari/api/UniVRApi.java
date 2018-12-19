package me.francescotonini.univrorari.api;

import java.util.List;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Lesson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UniVRApi {
    @GET("courses")
    Call<List<Course>> getCourses();

    @GET("academicyear/{academicYearId}/course/{courseId}/year/{courseYearId}/lessons")
    Call<List<Lesson>> getLessons(@Path("academicYearId") String academicYearId, @Path("courseId") String courseId,
                                  @Path("courseYearId") String courseYearId, @Query("month") int month,
                                  @Query("year") int year);
}
