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

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Helps handle cache
 */
public class CacheHelper {
    // This is solution is not ideal for large amount of data but atm I'm in a hurry so this looks the ideal solution
    // The best solution would be a local database (e.g. Room)

    /**
     * Sets a string given the key. If a value is already stored under the key provided, it will be overwritten
     * @param key key
     * @param value value to store
     */
    public static void set(String key, String value) {
        Prefs.putString(key, value);
    }

    /**
     * Gets the string under the key provided. If no value is available, returns the default value provided
     * @param key key
     * @param defaultValue default value if none is available
     * @return the string under the key provided. If no value is available, returns the default value provided
     */
    public static String get(String key, String defaultValue) {
        return Prefs.getString(key, defaultValue);
    }

    /**
     * Indicates whether a value under the key provided is available
     * @param key key
     * @return TRUE if value is available; otherwise FALSE
     */
    public static boolean contains(String key) {
        return Prefs.contains(key);
    }
}
