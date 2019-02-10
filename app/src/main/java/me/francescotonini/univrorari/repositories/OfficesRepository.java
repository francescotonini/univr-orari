package me.francescotonini.univrorari.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import java.util.List;
import me.francescotonini.univrorari.AppExecutors;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.api.ApiError;
import me.francescotonini.univrorari.api.UniVRApi;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.models.Office;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles communication between data and view model
 */
public class OfficesRepository extends BaseRepository {
    /**
     * Initializes a new instance of this class
     *
     * @param appExecutors thread pool
     * @param api instance of {@link UniVRApi}
     */
    public OfficesRepository(AppExecutors appExecutors, UniVRApi api) {
        super(appExecutors, api);

        offices = new MutableLiveData<>();
    }

    /**
     * Gets an {@link ApiResponse} with a list of {@link Office}
     * @return if {@link ApiResponse} has the error property initialized, something went wrong; otherwise data contains the result of the request
     */
    public LiveData<ApiResponse<List<Office>>> getOffices() {
        loadOffices();
        return offices;
    }

    private void loadOffices() {
        getAppExecutors().networkIO().execute(() -> getApi().getOffices().enqueue(new Callback<List<Office>>() {
            @Override public void onResponse(Call<List<Office>> call, Response<List<Office>> response) {
                if (!response.isSuccessful()) {
                    Logger.e(OfficesRepository.class.getSimpleName(), String.format("Unable to get offices because error code is %s ", response.code()));
                    offices.setValue(new ApiResponse<>(ApiError.BAD_RESPONSE));
                    return;
                }

                Logger.i(OfficesRepository.class.getSimpleName(), String.format("Got %s offices", response.body().size()));
                offices.setValue(new ApiResponse<>(response.body()));
            }

            @Override public void onFailure(Call<List<Office>> call, Throwable t) {
                Logger.e(OfficesRepository.class.getSimpleName(), "Unable to get offices: " + t.getMessage());
                offices.setValue(new ApiResponse<>(ApiError.NO_CONNECTION));
            }
        }));
    }

    private MutableLiveData<ApiResponse<List<Office>>> offices;
}
