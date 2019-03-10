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

package me.francescotonini.univrorari.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ActivityLessonDetailsBinding;
import me.francescotonini.univrorari.models.Lesson;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

/**
 * Activity class for R.id.activity_lesson_details
 */
public class LessonDetailsActivity extends BaseActivity {
    @Override protected int getLayoutId() {
        return R.layout.activity_lesson_details;
    }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Setup Toolbar + add back button
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Lesson lesson = (new Gson()).fromJson(getIntent().getStringExtra("lesson"), Lesson.class);
        Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        startTime.setTimeInMillis(lesson.getStartTimestamp());
        binding.viewLessonActivityDateTxt.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(startTime.getTime()));
        binding.viewLessonActivityLessonTxt.setText(lesson.getName());
        binding.viewLessonActivityRoomTxt.setText(lesson.getRoom());
        binding.viewLessonActivityTeacherTxt.setText(lesson.getTeacher());
    }

    private ActivityLessonDetailsBinding binding;
}

