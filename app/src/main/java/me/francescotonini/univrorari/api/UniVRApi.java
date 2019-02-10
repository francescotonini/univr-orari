package me.francescotonini.univrorari.api;

import java.util.List;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.models.Room;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Defines the interface between the app and the API
 */
public interface UniVRApi {
    @GET("courses")
    Call<List<Course>> getCourses();

    @GET("academicyear/{academicYearId}/course/{courseId}/year/{courseYearId}/lessons")
    Call<List<Lesson>> getLessons(@Path("academicYearId") String academicYearId, @Path("courseId") String courseId,
                                  @Path("courseYearId") String courseYearId, @Query("month") int month,
                                  @Query("year") int year);

    @GET("offices")
    Call<List<Office>> getOffices();

    @GET("offices/{id}/rooms")
    Call<List<Room>> getRooms(@Path("id") String id);
}
