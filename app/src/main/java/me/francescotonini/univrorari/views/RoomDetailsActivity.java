package me.francescotonini.univrorari.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ActivityRoomDetailsBinding;
import me.francescotonini.univrorari.helpers.DateTimeInterpreter;
import me.francescotonini.univrorari.models.Room;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

public class RoomDetailsActivity extends BaseActivity implements MonthLoader.MonthChangeListener {

    @Override protected int getLayoutId() {
        return R.layout.activity_room_details;
    }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Setup Toolbar
        setSupportActionBar((Toolbar) binding.toolbar);

        // Get room from intent
        room = (new Gson()).fromJson(getIntent().getStringExtra("room"), Room.class);

        // Bind listener for month changes
        binding.activityRoomDetailsWeekview.setMonthChangeListener(this);
        binding.activityRoomDetailsWeekview.setDateTimeInterpreter(new DateTimeInterpreter());
    }

    @Override public List<WeekViewEvent<Room.Event>> onMonthChange(Calendar startDate, Calendar endDate) {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) != startDate.get(Calendar.YEAR) || today.get(Calendar.MONTH) != startDate.get(Calendar.MONTH)) {
            return new ArrayList<>();
        }

        if (room.getEvents() == null) {
            return new ArrayList<>();
        }

        List<WeekViewEvent<Room.Event>> result = new ArrayList<>();
        for (Room.Event e : room.getEvents()) {
            result.add(e.toWeekViewEvent());
        }

        return result;
    }

    private Room room;
    private ActivityRoomDetailsBinding binding;
}
