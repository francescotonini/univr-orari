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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.gson.Gson;
import java.util.List;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.CoursesAdapter;
import me.francescotonini.univrorari.databinding.ActivitySetupSelectCourseBinding;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.helpers.SimpleDividerItemDecoration;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.viewmodels.CoursesViewModel;

public class SetupSelectCourseActivity extends BaseActivity implements CoursesAdapter.OnItemClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup_select_course;
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
        ((Toolbar)binding.toolbar).setSubtitle(R.string.activity_setup_select_course_description);

        // Set layout manager and divider
        binding.activitySetupSelectCoursesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.activitySetupSelectCoursesRecyclerview.addItemDecoration(new SimpleDividerItemDecoration(getApplication().getApplicationContext(), this.getResources().getColor(R.color.divider), 3));

        // Start progress bar
        binding.activitySetupSelectCourseRefreshlayout.setRefreshing(true);
        binding.activitySetupSelectCourseRefreshlayout.setEnabled(true);

        // Start observing
        getViewModel().getCourses().observe(this, coursesHandler);
    }

    @Override public void onItemClick(Course course) {
        Intent goToSetupSelectYearsActivity = new Intent(this, SetupSelectYearsActivity.class);
        goToSetupSelectYearsActivity.putExtra("course", (new Gson()).toJson(course));

        startActivity(goToSetupSelectYearsActivity);
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setup_select_course, menu);

        MenuItem search = menu.findItem(R.id.menu_setup_select_course_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(searchTextListener);

        return true;
    }

    private SearchView.OnQueryTextListener searchTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            ((CoursesAdapter)binding.activitySetupSelectCoursesRecyclerview.getAdapter()).getFilter().filter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            ((CoursesAdapter)binding.activitySetupSelectCoursesRecyclerview.getAdapter()).getFilter().filter(newText);
            return true;
        }
    };

    private CoursesViewModel viewModel;
    private ActivitySetupSelectCourseBinding binding;

    private Observer<ApiResponse<List<Course>>> coursesHandler = (response -> {
        if (response == null || !response.isSuccessful()) {
            Logger.e(SetupSelectCourseActivity.class.getSimpleName(), "Error on getCourses() response. App is going to close.");

            binding.activitySetupSelectCourseRefreshlayout.setEnabled(false);
            binding.activitySetupSelectCourseRefreshlayout.setRefreshing(false);

            DialogHelper.show(this, R.string.error_generic_title, R.string.error_generic_message, R.string.error_generic_close_button, (dialog, which) -> {
                finishAffinity();
            });
        }
        else {
            binding.activitySetupSelectCourseRefreshlayout.setRefreshing(false);
            binding.activitySetupSelectCourseRefreshlayout.setEnabled(false);

            binding.activitySetupSelectCoursesRecyclerview.setAdapter(new CoursesAdapter(response.getData(), this));
        }
    });
}
