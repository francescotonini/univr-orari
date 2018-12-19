package me.francescotonini.univrorari.views;

import android.content.Intent;
import android.os.Bundle;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

/**
 * Startup activity
 */
public class BootstrapActivity extends BaseActivity {
    @Override protected int getLayoutId() {
        return 0;
    }

    @Override protected void setToolbar() { }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override protected void setBinding() { }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PreferenceHelper.getBoolean(PreferenceHelper.Keys.DID_FIRST_BOOT)) {
            // TODO: move this somewhere else. One day I'll forget about this while trying to figure out a bug
            PreferenceHelper.setInt(PreferenceHelper.Keys.DAYS_TO_SHOW, 3);

            startActivity(new Intent(this, SelectCourseActivity.class));
        }
        else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}

