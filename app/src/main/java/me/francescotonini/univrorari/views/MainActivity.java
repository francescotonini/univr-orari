package me.francescotonini.univrorari.views;

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
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.databinding.ActivityMainBinding;
import me.francescotonini.univrorari.helpers.DateTimeInterpreter;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.helpers.SnackBarHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.viewmodels.LessonsViewModel;

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

        // If first boot, go to SelectCourseActivity
        if (!PreferenceHelper.getBoolean(PreferenceHelper.Keys.TIMETABLE_DID_FIRST_START)) {
            startActivity(new Intent(this, SelectCourseActivity.class));
        }

        binding.activityMainWeekview.setNumberOfVisibleDays(PreferenceHelper.getInt(PreferenceHelper.Keys.TIMETABLE_DAYS_TO_SHOW, 3));
        binding.activityMainWeekview.setDateTimeInterpreter(new DateTimeInterpreter());
        binding.activityMainWeekview.setMonthChangeListener(this);
        binding.activityMainWeekview.setOnEventClickListener(this);
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
            PreferenceHelper.setInt(PreferenceHelper.Keys.TIMETABLE_DAYS_TO_SHOW, 3);
            binding.activityMainWeekview.setNumberOfVisibleDays(3);
        } else if (item.getItemId() == R.id.menu_main_day_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.TIMETABLE_DAYS_TO_SHOW, 1);
            binding.activityMainWeekview.setNumberOfVisibleDays(1);
        } else if (item.getItemId() == R.id.menu_main_refresh) {
            getViewModel().clear();
            binding.activityMainWeekview.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.menu_main_rooms) {
            startActivity(new Intent(this, RoomsActivity.class));
        } else if (item.getItemId() == R.id.menu_main_settings) {
            // TODO: settings
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
        if (!PreferenceHelper.getBoolean(PreferenceHelper.Keys.TIMETABLE_DID_FIRST_START)) {
            return events;
        }

        // Load lessons
        if (!getViewModel().getLessons(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)).hasObservers()) {
            SnackBarHelper.show(binding.activityMainWeekview, R.string.loading);

            getViewModel().getLessons(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)).observe(this, this);
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

