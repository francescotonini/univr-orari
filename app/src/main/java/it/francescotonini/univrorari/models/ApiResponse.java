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

package it.francescotonini.univrorari.models;

import java.security.InvalidParameterException;
import it.francescotonini.univrorari.api.ApiError;

/**
 * Represents a response from the API
 * @param <T> type of the body of successful responses
 */
public class ApiResponse<T> {
    /**
     * Listener for api response
     */
    public interface ApiResponseListener {
        void onResponse();
        void onError(ApiError error);
    }

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
