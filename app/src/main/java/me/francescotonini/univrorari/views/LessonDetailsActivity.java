package me.francescotonini.univrorari.views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ActivityLessonDetailsBinding;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

/**
 * Activity class for R.id.activity_lesson_details
 */
public class LessonDetailsActivity extends BaseActivity {
    @Override protected int getLayoutId() {
        return R.layout.activity_lesson_details;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
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

        Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        startTime.setTimeInMillis(getIntent().getLongExtra("startTime", 0));

        binding.viewLessonActivityDateTxt.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(startTime.getTime()));
        binding.viewLessonActivityLessonTxt.setText(getIntent().getStringExtra("lesson"));
        binding.viewLessonActivityRoomTxt.setText(getIntent().getStringExtra("room"));
        binding.viewLessonActivityTeacherTxt.setText(getIntent().getStringExtra("teacher"));
    }

    private ActivityLessonDetailsBinding binding;
}

