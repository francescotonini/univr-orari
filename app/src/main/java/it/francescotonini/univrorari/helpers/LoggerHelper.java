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

package it.francescotonini.univrorari.helpers;

import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import it.francescotonini.univrorari.Logger;

public class LoggerHelper {
    /**
     * Builds the singleton of {@link LoggerHelper}
     * @param applicationContext context of the application
     */
    public static void build(Context applicationContext) {
        instance = new LoggerHelper(FirebaseAnalytics.getInstance(applicationContext));
    }

    /**
     * Returns an instance of {@link LoggerHelper}.
     * @return an instance of {@link LoggerHelper} if build() has been called before. Otherwise throws a {@link IllegalStateException}
     */
    public static LoggerHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("This class has not been initialized. Please call build() before this function");
        }

        return instance;
    }

    /**
     * Logs a network error to firebase
     * @param url url of the failed request
     * @param reason reason of the failed request
     * @param statusCode status code of the failed request
     * @param body body of the failed request
     */
    public void logNetworkError(String url, String reason, int statusCode, String body) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("reason", reason);
        bundle.putInt("statusCode", statusCode);
        bundle.putString("body", body);

        log(EVENT_TYPE.NETWORK_ERROR, bundle);
    }

    /**
     * Logs a json parse error
     * @param where identification of the class where this exception get caught
     * @param msg message
     */
    public void logJsonParseError(String where, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("where", where);
        bundle.putString("reason", msg);

        log(EVENT_TYPE.JSON_PARSE_ERROR, bundle);
    }

    /**
     * Logs a general error
     * @param where identification of the class where this exception get caught
     * @param msg message
     */
    public void logUnknownError(String where, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("where", where);
        bundle.putString("reason", msg);

        log(EVENT_TYPE.UNKNOWN_ERROR, bundle);
    }

    private enum EVENT_TYPE {
        NETWORK_ERROR,
        JSON_PARSE_ERROR,
        UNKNOWN_ERROR
    }

    private LoggerHelper(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    private void log(EVENT_TYPE type, Bundle params) {
        Logger.v(LoggerHelper.class.getSimpleName(), "Logging " + type.name() + " to firebase");

        firebaseAnalytics.logEvent(type.name(), params);
    }

    private final FirebaseAnalytics firebaseAnalytics;
    private static LoggerHelper instance;
}
