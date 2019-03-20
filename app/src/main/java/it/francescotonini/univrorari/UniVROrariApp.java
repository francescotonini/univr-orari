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

import android.app.Application;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import com.pixplicity.easyprefs.library.Prefs;

import androidx.appcompat.app.AppCompatDelegate;
import it.francescotonini.univrorari.helpers.PreferenceHelper;

/**
 * Main class. Let's you access to singletons
 */
public class UniVROrariApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // This is a class with different thread pools
        appExecutors = new AppExecutors();

        // Build preferences
        new Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(BuildConfig.APPLICATION_ID)
            .setUseDefaultSharedPreference(true)
            .build();

        AppCompatDelegate.setDefaultNightMode(PreferenceHelper.getInt(PreferenceHelper.Keys.UI_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
    }

    /**
     * Gets an instance of {@link DataRepository}
     * @return an instance of {@link DataRepository}
     */
    public DataRepository getDataRepository() {
        return DataRepository.getInstance(getApplicationContext(), appExecutors);
    }

    /**
     * Gets an instance of {@link AppExecutors}
     * @return an instance of {@link AppExecutors}
     */
    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    private AppExecutors appExecutors;
}
