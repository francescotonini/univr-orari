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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.databinding.ActivityRoomDetailsBinding;
import it.francescotonini.univrorari.helpers.DateTimeInterpreter;
import it.francescotonini.univrorari.models.Room;
import it.francescotonini.univrorari.viewmodels.BaseViewModel;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private Room room;
    private ActivityRoomDetailsBinding binding;
}
