package me.francescotonini.univrorari.models;

import java.security.InvalidParameterException;

import me.francescotonini.univrorari.api.ApiError;

/**
 * Represents a response from the API
 * @param <T> type of the body of successful responses
 */
public class ApiResponse<T> {
    /**
     * Initializes a new instance of this class with the body of the successful response
     * @param data body of the successful response
     */
    public ApiResponse(T data) {
        // Data can't be null
        if (data == null) {
            throw new InvalidParameterException("Data can't be null!");
        }

        this.data = data;
        this.error = null;
    }

    /**
     * Initializes a new instance of this class with a value from {@link ApiError}
     * @param error a value from {@link ApiError}
     */
    public ApiResponse(ApiError error) {
        if (error == null) {
            throw new InvalidParameterException("Error can't be null");
        }

        this.data = null;
        this.error = error;
    }

    /**
     * Gets the data. Can be null if response is unsuccessful
     * @return an object with the data from the response. Can be null if response is unsuccessful
     */
    public T getData() {
        return data;
    }

    /**
     * Gets the error encountered during the request. Can be null if response is successful
     * @return the error encountered during the request. Can be null if response is successful
     */
    public ApiError getError() {
        return error;
    }

    /**
     * Indicates whether the response is successful or not
     * @return TRUE if response is successful; otherwise FALSE
     */
    public boolean isSuccessful() {
        return data != null;
    }

    private final T data;
    private final ApiError error;
}
