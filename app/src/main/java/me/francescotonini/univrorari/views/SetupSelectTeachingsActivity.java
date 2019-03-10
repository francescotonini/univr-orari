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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.TeachingsAdapter;
import me.francescotonini.univrorari.databinding.ActivitySetupSelectTeachingsBinding;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.helpers.SimpleDividerItemDecoration;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Teaching;
import me.francescotonini.univrorari.models.Year;
import me.francescotonini.univrorari.viewmodels.CoursesViewModel;

public class SetupSelectTeachingsActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup_select_teachings;
    }

    @Override protected CoursesViewModel getViewModel() {
        if (viewModel == null) {
            CoursesViewModel.Factory factory = new CoursesViewModel.Factory(getApplication(), ((UniVROrariApp)getApplication()).getDataRepository().getCoursesRepository());
            viewModel = ViewModelProviders.of(this, factory).get(CoursesViewModel.class);
        }

        return viewModel;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Add toolbar + subtitle
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((Toolbar)binding.toolbar).setSubtitle(R.string.activity_setup_select_teachings_description);

        // Set layout manager and divider
        binding.activitySetupSelectTeachingsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.activitySetupSelectTeachingsRecyclerview.addItemDecoration(new SimpleDividerItemDecoration(getApplication().getApplicationContext(), this.getResources().getColor(R.color.divider), 3));

        if (!getIntent().hasExtra("course") || !getIntent().hasExtra("selectedYears")) {
            onBackPressed();
            return;
        }

        // Start progress bar
        binding.activitySetupSelectTeachingsRefreshlayout.setRefreshing(true);
        binding.activitySetupSelectTeachingsRefreshlayout.setEnabled(true);

        course = new Gson().fromJson(getIntent().getStringExtra("course"), Course.class);
        selectedYears = new Gson().fromJson(getIntent().getStringExtra("selectedYears"), new TypeToken<List<Year>>(){}.getType());

        // Click listener for the save button
        binding.activitySetupSelectTeachingsSaveButton.setEnabled(false);
        binding.activitySetupSelectTeachingsSaveButton.setOnClickListener(saveButtonClickListener);

        // Get list of offices to show + start animation
        binding.activitySetupSelectTeachingsRefreshlayout.setRefreshing(true);
        getViewModel().getTeachings(course.getAcademicYearId(), course.getId()).observe(this, teachingsObserver);
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private ActivitySetupSelectTeachingsBinding binding;
    private CoursesViewModel viewModel;
    private Course course;
    private List<Year> selectedYears;

    private View.OnClickListener saveButtonClickListener = click -> {
        List<Teaching> teachings = ((TeachingsAdapter)binding.activitySetupSelectTeachingsRecyclerview.getAdapter()).getSelectedTeachings();
        getViewModel().savePreferences(course, teachings);

        Intent goToSetupSelectOffices = new Intent(this, SetupSelectOfficesActivity.class);
        startActivity(goToSetupSelectOffices);
    };

    private Observer<ApiResponse<List<Teaching>>> teachingsObserver = teachings -> {
        if (!teachings.isSuccessful()) {
            DialogHelper.show(this, R.string.error_network_title, R.string.error_network_message, R.string.error_network_button_message);

            // Stop progress bar
            binding.activitySetupSelectTeachingsRefreshlayout.setRefreshing(false);
            binding.activitySetupSelectTeachingsRefreshlayout.setEnabled(false);

            onBackPressed();
            return;
        }

        binding.activitySetupSelectTeachingsRecyclerview.setAdapter(new TeachingsAdapter(teachings.getData(), selectedYears));

        // Update UI accordingly
        binding.activitySetupSelectTeachingsSaveButton.setEnabled(true);
        binding.activitySetupSelectTeachingsRefreshlayout.setRefreshing(false);
        binding.activitySetupSelectTeachingsRefreshlayout.setEnabled(false);
    };

}
