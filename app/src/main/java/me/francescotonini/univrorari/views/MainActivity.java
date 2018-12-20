package me.francescotonini.univrorari.views;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ActivityMainBinding;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

/**
 * Code behind of R.layout.activity_main
 */
public class MainActivity extends BaseActivity {
    @Override protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // By default, show TimetableFragment
        visibleFragmentType = VisibleFragment.TIMETABLE;

        // React to bottom bar tap
        binding.activityMainBottomnavigationview.setOnNavigationItemSelectedListener((click) -> {
            visibleFragmentType = click.getItemId() == R.id.menu_main_bottom_bar_timetable ? VisibleFragment.TIMETABLE : VisibleFragment.ROOMS;
            loadFragment();

            return true;
        });
    }

    @Override protected void onResume() {
        super.onResume();

        loadFragment();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_top_bar, menu);

        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        if (visibleFragmentType == VisibleFragment.ROOMS) {
            menu.removeItem(R.id.menu_main_week_view);
            menu.removeItem(R.id.menu_main_day_view);
        }
        else if (visibleFragmentType == VisibleFragment.TIMETABLE) {
            int daysToShow = PreferenceHelper.getInt(PreferenceHelper.Keys.DAYS_TO_SHOW);
            timetableFragment.setNumberOfVisibleDays(daysToShow);

            if (daysToShow == 1) {
                menu.removeItem(R.id.menu_main_day_view);
            }
            else {
                menu.removeItem(R.id.menu_main_week_view);
            }
        }

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_week_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.DAYS_TO_SHOW, 3);
            invalidateOptionsMenu();
        }
        else if (item.getItemId() == R.id.menu_main_day_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.DAYS_TO_SHOW, 1);
            invalidateOptionsMenu();
        }
        else if (item.getItemId() == R.id.menu_main_change_course) {
            Intent intent = new Intent(this, SelectCourseActivity.class);
            intent.putExtra("showBackButton", true);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_main_refresh) {
            visibleFragment.refresh();
        }
        else if (item.getItemId() == R.id.menu_main_settings) {
            // TODO: settings
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment() {
        if (visibleFragmentType == VisibleFragment.TIMETABLE) {
            if (timetableFragment == null) {
                timetableFragment = new TimetableFragment();
            }

            visibleFragment = timetableFragment;
        }
        // else...

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_framelayout, visibleFragment)
                .addToBackStack(null)
                .commit();

        // This will recreate the option menu so that we can remove icons not necessary
        invalidateOptionsMenu();
    }

    private enum VisibleFragment {
        TIMETABLE,
        ROOMS
    }

    private ActivityMainBinding binding;
    private TimetableFragment timetableFragment;
    // roomsFragment...
    private BaseFragment visibleFragment;
    private VisibleFragment visibleFragmentType;
}

