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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.alamkanak.weekview.EventClickListener;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import it.francescotonini.univrorari.Logger;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.UniVROrariApp;
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
public class MainActivity extends BaseActivity implements Observer<ApiResponse<List<Lesson>>>, EventClickListener<Lesson>, MonthLoader.MonthChangeListener {
    @Override protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override protected LessonsViewModel getViewModel() {
        if (viewModel == null) {
            LessonsViewModel.Factory factory = new LessonsViewModel.Factory(getApplication(),
                    ((UniVROrariApp)getApplication()).getDataRepository().getLessonsRepository());
            viewModel = ViewModelProviders.of(this, factory).get(LessonsViewModel.class);
        }

        return viewModel;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Setup Toolbar
        setSupportActionBar((Toolbar) binding.toolbar);

        // Setup week view
        binding.activityMainWeekview.setNumberOfVisibleDays(PreferenceHelper.getInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 3));
        binding.activityMainWeekview.setDateTimeInterpreter(new DateTimeInterpreter());
        binding.activityMainWeekview.setMonthChangeListener(this);
        binding.activityMainWeekview.setOnEventClickListener(this);
        binding.activityMainWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        // Setup rooms click event
        binding.activityMainRoomsButton.setOnClickListener((click) -> startActivity(new Intent(this, RoomsActivity.class)));

        if (getIntent().hasExtra("clear")) {
            getViewModel().clear();
            binding.activityMainWeekview.notifyDataSetChanged();
        }
    }

    @Override protected void onResume() {
        super.onResume();

        binding.activityMainWeekview.notifyDataSetChanged();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_week_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 3);
            binding.activityMainWeekview.setNumberOfVisibleDays(3);
        } else if (item.getItemId() == R.id.menu_main_day_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.WEEKVIEW_DAYS_TO_SHOW, 1);
            binding.activityMainWeekview.setNumberOfVisibleDays(1);
        } else if (item.getItemId() == R.id.menu_main_refresh) {
            getViewModel().clear();
            binding.activityMainWeekview.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.menu_main_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return true;
    }

    @Override public void onChanged(@Nullable ApiResponse<List<Lesson>> lessons) {
        if (!lessons.isSuccessful()) {
            DialogHelper.show(this, R.string.error_network_title, R.string.error_network_message, R.string.error_network_button_message);

            return;
        }
        else if (lessons.getData().size() == 0) {
            Logger.v(MainActivity.class.getSimpleName(), "Ignoring onChanged event because list is NULL or empty");

            return;
        }

        binding.activityMainWeekview.notifyDataSetChanged();
    }

    @Override public List<WeekViewDisplayable<Lesson>> onMonthChange(Calendar startDate, Calendar endDate) {
        List<WeekViewDisplayable<Lesson>> events = new ArrayList<>();

        // Stop here if this is first boot
        if (!PreferenceHelper.getBoolean(PreferenceHelper.Keys.DID_FIRST_BOOT)) {
            return events;
        }

        // Load lessons
        if (!getViewModel().getLessons(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)).hasObservers()) {
            SnackBarHelper.show(binding.activityMainWeekview, R.string.loading);

            getViewModel().getLessons(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)).observe(this, this);
            return events;
        }

        ApiResponse<List<Lesson>> response = getViewModel().getLessons(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)).getValue();
        if (response == null) {
            return events;
        }

        List<Lesson> lessons = getViewModel().getLessons(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)).getValue().getData();
        if (lessons == null) {
            return events;
        }

        for (Lesson lesson: lessons) {
            if (lesson.getName() == null || lesson.getRoom() == null) {
                Logger.e(MainActivity.class.getSimpleName(), "Ignoring lesson because name or room is NULL");
                continue;
            }

            WeekViewDisplayable<Lesson> event = lesson.toWeekViewEvent();
            ((WeekViewEvent) event).setColor(getViewModel().getLessonColor(lesson.getName()));
            events.add(event);
        }

        return events;
    }

    @Override public void onEventClick(Lesson lesson, RectF eventRect) {
        Intent intent = new Intent(this, LessonDetailsActivity.class);
        intent.putExtra("lesson", (new Gson()).toJson(lesson));
        startActivity(intent);
    }

    private ActivityMainBinding binding;
    private LessonsViewModel viewModel;
}

