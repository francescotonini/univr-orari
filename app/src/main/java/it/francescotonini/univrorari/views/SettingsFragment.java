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

package it.francescotonini.univrorari.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.gson.Gson;

import androidx.preference.SwitchPreference;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.helpers.DialogHelper;
import it.francescotonini.univrorari.viewmodels.SettingsViewModel;

@SuppressLint("ValidFragment")
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    public SettingsFragment(SettingsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeTeachings = findPreference("change_teachings");
        changeOffices = findPreference("change_offices");
        // clearCache = findPreference("clear_cache");
        appVersion = findPreference("app_version");
        darkTheme = (SwitchPreference)findPreference("dark_theme");

        try {
            String version = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
            appVersion.setSummary(version + System.getProperty("line.separator") + System.getProperty("line.separator") + appVersion.getSummary());
        } catch(PackageManager.NameNotFoundException ignored) { }
    }

    @Override
    public void onResume() {
        super.onResume();

        changeTeachings.setOnPreferenceClickListener(this);
        changeOffices.setOnPreferenceClickListener(this);
        // clearCache.setOnPreferenceClickListener(this);
        darkTheme.setOnPreferenceChangeListener(this);

        int currentNightMode = viewModel.getUITheme();
        darkTheme.setDefaultValue(currentNightMode == AppCompatDelegate.MODE_NIGHT_YES);

        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            darkTheme.setSummary(R.string.settings_enable_dark_theme_true);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey() == changeTeachings.getKey()) {
            Intent goToSetupSelectYears = new Intent(getActivity(), SetupSelectYearsActivity.class);
            goToSetupSelectYears.putExtra("course", new Gson().toJson(viewModel.getCourse()));

            startActivity(goToSetupSelectYears);
        }
        else if (preference.getKey() == changeOffices.getKey()) {
            startActivity(new Intent(getActivity(), SetupSelectOfficesActivity.class));
        }

        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey() == darkTheme.getKey()) {

            if ((Boolean) newValue) {
                viewModel.setUITheme(AppCompatDelegate.MODE_NIGHT_YES);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                darkTheme.setSummary(R.string.settings_enable_dark_theme_true);
            }
            else {
                viewModel.setUITheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);


                darkTheme.setSummary(R.string.settings_enable_dark_theme_false);
            }

            // Show popup
            DialogHelper.show(getContext(), R.string.info, R.string.settings_enable_dark_theme_message, R.string.ok);
        }

        return true;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }

    private SettingsViewModel viewModel;
    private Preference changeTeachings;
    private Preference changeOffices;
    // private Preference clearCache;
    private Preference appVersion;
    private SwitchPreference darkTheme;
}
