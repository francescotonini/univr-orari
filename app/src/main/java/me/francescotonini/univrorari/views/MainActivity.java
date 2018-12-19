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
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.databinding.ActivityMainBinding;
import me.francescotonini.univrorari.helpers.DateTimeInterpreter;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.helpers.SnackBarHelper;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.viewmodels.LessonsViewModel;

/**
 * Code behind of R.layout.activity_main
 */
public class MainActivity extends BaseActivity implements MonthLoader.MonthChangeListener,
        Observer<List<Lesson>>, WeekView.EventClickListener {
    @Override protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override protected LessonsViewModel getViewModel() {
        if (viewModel == null) {
            LessonsViewModel.Factory factory = new LessonsViewModel.Factory(getApplication(), ((UniVROrariApp)getApplication()).getDataRepository().getLessonsRepository());
            viewModel = ViewModelProviders.of(this, factory).get(LessonsViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.activityMainBottomnavigationview.setOnNavigationItemSelectedListener((click) -> {
            if (click.getItemId() == R.id.menu_main_bottom_bar_rooms) {
                // TODO: show room
                return false;
            }

            return true;
        });

        binding.activityMainWeekview.setOnEventClickListener(this);
        binding.activityMainWeekview.setDateTimeInterpreter(new DateTimeInterpreter());
        binding.activityMainWeekview.setMonthChangeListener(this);
        updateView();
    }

    @Override protected void onResume() {
        super.onResume();

        binding.activityMainWeekview.notifyDatasetChanged();
    }

    @Override public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        if (!getViewModel().getLessons(newMonth, newYear).hasObservers()) {
            SnackBarHelper.show(binding.activityMainWeekview, R.string.loading);

            Logger.i(MainActivity.class.getSimpleName(), String.format("Subscribing to %s-%s ", newYear, newMonth));
            getViewModel().getLessons(newMonth, newYear).observe(this, this);

            return events;
        } else {
            Logger.i(MainActivity.class.getSimpleName(), String.format("Already subscribed to %s-%s ", newYear, newMonth));
        }

        List<Lesson> lessons = getViewModel().getLessons(newMonth, newYear).getValue();
        if (lessons == null) {
            Logger.w(MainActivity.class.getSimpleName(), String.format("%s-%s is NULL", newYear, newMonth));
            return events;
        }
        else {
            Logger.i(MainActivity.class.getSimpleName(), String.format("%s-%s has %s events", newYear, newMonth, lessons.size()));
        }

        for (Lesson lesson: lessons) {
            if (lesson.getName() == null || lesson.getRoom() == null) {
                Logger.e(MainActivity.class.getSimpleName(), "Ignoring lesson because name or room is NULL");
                continue;
            }

            Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            startTime.setTimeInMillis(lesson.getStartTimestamp());
            Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            endTime.setTimeInMillis(lesson.getEndTimestamp());
            endTime.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE) - 1);

            WeekViewEvent event = new WeekViewEvent(startTime.getTimeInMillis(), lesson.getName(),
                    lesson.getRoom(), startTime, endTime);
            event.setColor(getViewModel().getLessonColor(lesson.getName()));
            events.add(event);
        }

        return events;
    }

    @Override public void onChanged(@Nullable List<Lesson> lessons) {
        if (lessons == null || lessons.size() == 0) {
            Logger.v(MainActivity.class.getSimpleName(), "Ignoring onChanged event because list is NULL or empty");

            return;
        }

        binding.activityMainWeekview.notifyDatasetChanged();
    }

    @Override public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Lesson clickedLesson = getViewModel().getLesson(event.getStartTime(), event.getEndTime());
        if (clickedLesson == null) {
            Logger.e(MainActivity.class.getSimpleName(), "No lesson found");
            return;
        }

        Intent intent = new Intent(this, LessonDetailsActivity.class);
        intent.putExtra("lesson", clickedLesson.getName());
        intent.putExtra("room", clickedLesson.getRoom());
        intent.putExtra("teacher", clickedLesson.getTeacher());
        intent.putExtra("startTime", clickedLesson.getStartTimestamp());
        startActivity(intent);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_top_bar, menu);

        weekViewMenuItem = menu.findItem(R.id.menu_main_week_view);
        dayViewMenuItem = menu.findItem(R.id.menu_main_day_view);
        updateView();

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_week_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.DAYS_TO_SHOW, 3);
            updateView();
        }
        else if (item.getItemId() == R.id.menu_main_day_view) {
            PreferenceHelper.setInt(PreferenceHelper.Keys.DAYS_TO_SHOW, 1);
            updateView();
        }
        else if (item.getItemId() == R.id.menu_main_change_course) {
            Intent intent = new Intent(this, SelectCourseActivity.class);
            intent.putExtra("showBackButton", true);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_main_settings) {

        }
        else if (item.getItemId() == R.id.menu_main_refresh) {
            getViewModel().clear();
            binding.activityMainWeekview.notifyDatasetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        binding.activityMainWeekview.setNumberOfVisibleDays(PreferenceHelper.getInt(PreferenceHelper.Keys.DAYS_TO_SHOW));
        binding.activityMainWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        if (weekViewMenuItem != null && dayViewMenuItem != null) {
            weekViewMenuItem.setVisible(PreferenceHelper.getInt(PreferenceHelper.Keys.DAYS_TO_SHOW) == 1);
            dayViewMenuItem.setVisible(PreferenceHelper.getInt(PreferenceHelper.Keys.DAYS_TO_SHOW) > 1);
        }
    }

    private ActivityMainBinding binding;
    private LessonsViewModel viewModel;
    private MenuItem weekViewMenuItem;
    private MenuItem dayViewMenuItem;
}

