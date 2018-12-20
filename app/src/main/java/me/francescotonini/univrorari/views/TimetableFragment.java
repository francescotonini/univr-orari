package me.francescotonini.univrorari.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import me.francescotonini.univrorari.databinding.FragmentTimetableBinding;
import me.francescotonini.univrorari.helpers.DateTimeInterpreter;
import me.francescotonini.univrorari.helpers.SnackBarHelper;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.viewmodels.LessonsViewModel;

public class TimetableFragment extends BaseFragment implements MonthLoader.MonthChangeListener,
        Observer<List<Lesson>>, WeekView.EventClickListener {

    @Override protected int getLayoutId() {
        return R.layout.fragment_timetable;
    }

    @Override protected LessonsViewModel getViewModel() {
        if (viewModel == null) {
            LessonsViewModel.Factory factory = new LessonsViewModel.Factory(getActivity().getApplication(),
                    ((UniVROrariApp)getActivity().getApplication()).getDataRepository().getLessonsRepository());
            viewModel = ViewModelProviders.of(this, factory).get(LessonsViewModel.class);
        }

        return viewModel;
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        binding.fragmentTimetableWeekview.setOnEventClickListener(this);
        binding.fragmentTimetableWeekview.setDateTimeInterpreter(new DateTimeInterpreter());
        binding.fragmentTimetableWeekview.setMonthChangeListener(this);
        binding.fragmentTimetableWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        return binding.getRoot();
    }

    @Override public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        if (!getViewModel().getLessons(newMonth, newYear).hasObservers()) {
            SnackBarHelper.show(getActivity().findViewById(R.id.activity_main_framelayout), R.string.loading);

            getViewModel().getLessons(newMonth, newYear).observe(this, this);
            return events;
        }

        List<Lesson> lessons = getViewModel().getLessons(newMonth, newYear).getValue();
        if (lessons == null) {
            return events;
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

        binding.fragmentTimetableWeekview.notifyDatasetChanged();
    }

    @Override public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Lesson clickedLesson = getViewModel().getLesson(event.getStartTime(), event.getEndTime());
        if (clickedLesson == null) {
            Logger.e(MainActivity.class.getSimpleName(), "No lesson found");
            return;
        }

        Intent intent = new Intent(getActivity(), LessonDetailsActivity.class);
        intent.putExtra("lesson", clickedLesson.getName());
        intent.putExtra("room", clickedLesson.getRoom());
        intent.putExtra("teacher", clickedLesson.getTeacher());
        intent.putExtra("startTime", clickedLesson.getStartTimestamp());
        startActivity(intent);
    }

    public void setNumberOfVisibleDays(int days) {
        binding.fragmentTimetableWeekview.setNumberOfVisibleDays(days);
        binding.fragmentTimetableWeekview.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    @Override public void refresh() {
        getViewModel().clear();
        binding.fragmentTimetableWeekview.notifyDatasetChanged();
    }

    private FragmentTimetableBinding binding;
    private LessonsViewModel viewModel;
}
