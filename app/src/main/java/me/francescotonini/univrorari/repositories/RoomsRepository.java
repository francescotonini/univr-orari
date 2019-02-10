package me.francescotonini.univrorari.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.api.ApiError;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.models.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles communication between data and view model
 */
public class RoomsRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     */
    public RoomsRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);
        rooms = new MutableLiveData<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Room}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Room>>> getRooms() {
        loadRooms();
        return rooms;
    }

    /**
     * Reloads the data
     */
    public void reload() {
        if (rooms.getValue() != null && rooms.getValue().isSuccessful()) {
            rooms.getValue().getData().clear();
        }

        loadRooms();
    }

    private void loadRooms() {
        Logger.i(RoomsRepository.class.getSimpleName(), "Loading rooms");

        String[] offices = PreferenceHelper.getString(PreferenceHelper.Keys.ROOMS_OFFICES, "").split("-");

        // Stop if first item is empty
        if (offices[0] == "") return;

        for (String officeId: offices) {
            getAppExecutors().networkIO().execute(() -> getApi()
            .getRooms(officeId)
            .enqueue(new Callback<List<Room>>() {
                @Override public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                    if (!response.isSuccessful()) {
                        Logger.e(RoomsRepository.class.getSimpleName(), String.format("Unable to get rooms because error code is %s ", response.code()));
                        rooms.setValue(new ApiResponse<>(ApiError.BAD_RESPONSE));

                        return;
                    }

                    Logger.i(RoomsRepository.class.getSimpleName(), String.format("Got %s rooms", response.body().size()));

                    if (rooms.getValue() != null && rooms.getValue().isSuccessful()) {
                        rooms.getValue().getData().addAll(response.body());
                        rooms.setValue(new ApiResponse<>(rooms.getValue().getData()));
                    }
                    else {
                        rooms.setValue(new ApiResponse<>(response.body()));
                    }
                }

                @Override public void onFailure(Call<List<Room>> call, Throwable t) {
                    Logger.e(LessonsRepository.class.getSimpleName(), "Unable to get rooms: " + t.getMessage());
                    rooms.setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
                }
            }));
        }
    }

    private MutableLiveData<ApiResponse<List<Room>>> rooms;
}
