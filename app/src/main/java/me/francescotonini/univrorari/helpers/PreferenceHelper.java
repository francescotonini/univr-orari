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

package me.francescotonini.univrorari.helpers;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Helps create/modify/delete persistent preferences
 */
public class PreferenceHelper {
    /**
     * Storable keys
     */
    public enum Keys {
        COURSE,
        TEACHINGS,
        OFFICES,
        DID_FIRST_BOOT,
        WEEKVIEW_DAYS_TO_SHOW
    }

    /**
     * Sets a string given the key. If a value is already stored under the key provided, it will be overwrited
     * @param key key
     * @param value value to store
     */
    public static void setString(Keys key, String value) {
        Prefs.putString(keyToString(key), value);
    }

    /**
     * Sets a int given the key. If a value is already stored under the key provided, it will be overwrited
     * @param key key
     * @param value value to store
     */
    public static void setInt(Keys key, int value) {
        Prefs.putInt(keyToString(key), value);
    }

    /**
     * Sets a boolean give the key. If a value is already stored under the key provided, it will be overwrited
     * @param key key
     * @param value value
     */
    public static void setBoolean(Keys key, boolean value) {
        Prefs.putBoolean(keyToString(key), value);
    }

    /**
     * Gets the string under the key provided. If no value is available, returns NULL
     * @param key key
     * @param defaultValue default value if none is available
     * @return the string under the key provided. If no value is available, returns NULL
     */
    public static String getString(Keys key, String defaultValue) {
        return Prefs.getString(keyToString(key), defaultValue);
    }

    /**
     * Gets the string under the key provided. If no value is available, returns NULL
     * @param key key
     * @return the string under the key provided. If no value is available, returns NULL
     */
    public static String getString(Keys key) {
        return Prefs.getString(keyToString(key), null);
    }

    /**
     * Gets the int under the key provided. If no value is available, returns 0
     * @param key key
     * @return the int under the key provided. If no value is available, returns 0
     */
    public static int getInt(Keys key) {
        return Prefs.getInt(keyToString(key), 0);
    }

    /**
     * Gets the int under the key provided. If no value is available, returns 0
     * @param key key
     * @param defaultValue default value if none is available
     * @return the int under the key provided. If no value is available, returns 0
     */
    public static int getInt(Keys key, int defaultValue) {
        return Prefs.getInt(keyToString(key), defaultValue);
    }

    /**
     * Gets the boolean under the key provided. If no value is available, returns FALSE
     * @param key key
     * @return the boolean under the key provided. If no value is available, returns FALSE
     */
    public static boolean getBoolean(Keys key) {
        return Prefs.getBoolean(keyToString(key), false);
    }

    /**
     * Removes a value given the key
     * @param key key
     */
    public static void clear(Keys key) {
        Prefs.remove(keyToString(key));
    }

    private static String keyToString(Keys key) {
        return key.name();
    }
}
