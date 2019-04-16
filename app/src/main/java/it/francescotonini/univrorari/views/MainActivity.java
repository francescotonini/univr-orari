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

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alamkanak.weekview.EventClickListener;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.UniVROrariApp;
import it.francescotonini.univrorari.api.ApiError;
import it.francescotonini.univrorari.databinding.ActivityMainBinding;
import it.francescotonini.univrorari.helpers.DateTimeInterpreter;
import it.francescotonini.univrorari.helpers.DialogHelper;
import it.francescotonini.univrorari.helpers.PreferenceHelper;
import it.francescotonini.univrorari.helpers.SnackBarHelper;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Lesson;
import it.francescotonini.univrorari.viewmodels.LessonsViewModel;

/**
 * Code behind of R.layout.activity_main
 */
public class MainActivity extends BaseActivity implements EventClickListener<Lesson>, MonthLoader.MonthChangeListener, ApiResponse.ApiResponseListener {
    @Override protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override protected LessonsViewModel getViewModel() {
        if (viewModel == null) {
            LessonsViewModel.Factory factory = new LessonsViewModel.Factory(getApplication(),
                    ((UniVROrariApp)getApplication()).getDataRepository().getLessonsRepository(), this);
            viewModel = ViewModelProviders.of(this, factory).get(LessonsViewModel.class);
        }

        return viewModel;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Clear data (fixes theme change)
        getViewModel().clear();

        // Setup binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Setup Toolbar
        setSupportActionBar((Toolbar) binding.toolbar);

        // Setup week view
        binding.activityMainWeekview.setNumberOfVisibleDays(PreferenceHelper.getInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 3));
        binding.activityMainWeekview.setDateTimeInterpreter(new DateTimeInterpreter());
        binding.activityMainWeekview.setMonthChangeListener(this);
        binding.activityMainWeekview.setOnEventClickListener(this);

        // Setup rooms click event
        binding.activityMainRoomsButton.setOnClickListener((click) -> {
            if (!PreferenceHelper.getBoolean(PreferenceHelper.Keys.DID_SELECT_OFFICES)) {
                Intent selectOfficesIntent = new Intent(this, SetupSelectOfficesActivity.class);
                selectOfficesIntent.putExtra("isFirstBoot", true);

                startActivity(selectOfficesIntent);
                return;
            }

            startActivity(new Intent(this, RoomsActivity.class));
        });

        if (getIntent().hasExtra("clear")) {
            getViewModel().clear();
            binding.activityMainWeekview.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // -0.0 is midnight
        // This fixes a bug where theme change sets the current hour to midnight
        if (binding.activityMainWeekview.getFirstVisibleHour() == -0.0) {
            binding.activityMainWeekview.goToHour(8);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_rooms) {
            startActivity(new Intent(this, RoomsActivity.class));
        } else if (item.getItemId() == R.id.menu_main_three_day_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 3);
            binding.activityMainWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            binding.activityMainWeekview.setNumberOfVisibleDays(3);
        } else if (item.getItemId() == R.id.menu_main_week_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 5);
            binding.activityMainWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            binding.activityMainWeekview.setNumberOfVisibleDays(5);
        } else if (item.getItemId() == R.id.menu_main_day_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 1);
            binding.activityMainWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            binding.activityMainWeekview.setNumberOfVisibleDays(1);
        } else if (item.getItemId() == R.id.menu_main_refresh) {
            getViewModel().clear();
            binding.activityMainWeekview.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.menu_main_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return true;
    }

    @Override public void onResponse() {
        binding.activityMainWeekview.notifyDataSetChanged();
    }

    @Override public void onError(ApiError error) {
        if (!this.isFinishing()) {
            DialogHelper.show(this, R.string.error_network_title, R.string.error_network_message, R.string.error_network_button_message);
        }
    }

    @Override public List<WeekViewDisplayable<Lesson>> onMonthChange(Calendar startDate, Calendar endDate) {
        SnackBarHelper.show(binding.activityMainWeekview, R.string.loading);

        List<WeekViewDisplayable<Lesson>> events = getViewModel().getLessons(startDate, endDate);
        if (events == null) {
            return new ArrayList<>();
        }

        return  events;
    }

    @Override public void onEventClick(Lesson lesson, RectF eventRect) {
        Intent intent = new Intent(this, LessonDetailsActivity.class);
        intent.putExtra("lesson", (new Gson()).toJson(lesson));
        startActivity(intent);
    }

    private ActivityMainBinding binding;
    private LessonsViewModel viewModel;
}

