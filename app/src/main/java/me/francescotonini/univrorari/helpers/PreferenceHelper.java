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
        TIMETABLE_ACADEMIC_YEAR,
        TIMETABLE_COURSE,
        TIMETABLE_COURSE_YEAR,
        TIMETABLE_DAYS_TO_SHOW,
        TIMETABLE_DID_FIRST_START,
        ROOMS_DID_FIRST_START,
        ROOMS_OFFICES
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
