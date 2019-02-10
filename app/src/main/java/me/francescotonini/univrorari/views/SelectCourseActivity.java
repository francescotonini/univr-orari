package me.francescotonini.univrorari.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.util.List;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.CoursesAdapter;
import me.francescotonini.univrorari.databinding.ActivitySelectCourseBinding;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Year;
import me.francescotonini.univrorari.viewmodels.CoursesViewModel;

/**
 * Activity for R.layout.activity_select_course
 */
public class SelectCourseActivity extends BaseActivity implements CoursesAdapter.OnItemClickListener, Observer<ApiResponse<List<Course>>> {
    @Override protected int getLayoutId() {
        return R.layout.activity_select_course;
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

        // Setup toolbar and back button
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Start loading animation
        binding.activitySelectCoursesRefreshlayout.setRefreshing(true);

        getViewModel().getCourses().observe(this, this);
    }

    @Override public void onItemClick(Course course, Year year) {
        getViewModel().setCourse(course.getAcademicYearId(), year.getId(), course.getId());

        // Go back to main
        onBackPressed();
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override public void onChanged(@Nullable ApiResponse<List<Course>> response) {
        if (response == null || !response.isSuccessful()) {
            Logger.e(SelectCourseActivity.class.getSimpleName(), "Error on getCourses() response");

            DialogHelper.show(this, R.string.error_generic_title, R.string.error_generic_message, R.string.error_generic_close_button);
        }
        else {
            // This workaround prevents data incosistency
            // https://github.com/thoughtbot/expandable-recycler-view/issues/147
            binding.activitySelectCoursesRecyclerview.setAdapter(new CoursesAdapter(response.getData(), this));
        }

        // Stop the loading animations
        binding.activitySelectCoursesRefreshlayout.setEnabled(false);
        binding.activitySelectCoursesRefreshlayout.setRefreshing(false);
    }

    private ActivitySelectCourseBinding binding;
    private CoursesViewModel viewModel;
}
