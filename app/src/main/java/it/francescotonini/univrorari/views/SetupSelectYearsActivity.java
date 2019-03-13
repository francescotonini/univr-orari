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

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.google.gson.Gson;
import java.util.List;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.adapters.YearsAdapter;
import it.francescotonini.univrorari.databinding.ActivitySetupSelectYearsBinding;
import it.francescotonini.univrorari.helpers.SimpleDividerItemDecoration;
import it.francescotonini.univrorari.models.Course;
import it.francescotonini.univrorari.models.Year;
import it.francescotonini.univrorari.viewmodels.BaseViewModel;

public class SetupSelectYearsActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup_select_years;
    }

    @Override protected BaseViewModel getViewModel() {
        return null;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Add toolbar + subtitle
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((Toolbar)binding.toolbar).setSubtitle(R.string.activity_setup_select_years_description);

        // Set layout manager and divider
        binding.activitySetupSelectYearsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.activitySetupSelectYearsRecyclerview.addItemDecoration(new SimpleDividerItemDecoration(getApplication().getApplicationContext(), this.getResources().getColor(R.color.divider), 3));

        // Setup save button
        binding.activitySetupSelectYearsSaveButton.setOnClickListener(saveButtonClickListener);

        // Get intent data, otherwise go back
        if (!getIntent().hasExtra("course")) {
            onBackPressed();
            return;
        }

        // Parse and show data
        selectedCourse = new Gson().fromJson(getIntent().getStringExtra("course"), Course.class);
        binding.activitySetupSelectYearsRecyclerview.setAdapter(new YearsAdapter(selectedCourse.getYears()));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Course selectedCourse;
    private ActivitySetupSelectYearsBinding binding;

    private View.OnClickListener saveButtonClickListener = (click) -> {
        List<Year> selectedYears = ((YearsAdapter)binding.activitySetupSelectYearsRecyclerview.getAdapter()).getSelectedYears();

        Intent goToSelectTeachingsActivity = new Intent(this, SetupSelectTeachingsActivity.class);
        goToSelectTeachingsActivity.putExtra("course", new Gson().toJson(selectedCourse));
        goToSelectTeachingsActivity.putExtra("selectedYears", new Gson().toJson(selectedYears));
        startActivity(goToSelectTeachingsActivity);
    };
}
