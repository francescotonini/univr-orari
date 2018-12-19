package me.francescotonini.univrorari.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.CoursesAdapter;
import me.francescotonini.univrorari.databinding.ActivitySelectCourseBinding;
import me.francescotonini.univrorari.helpers.SnackBarHelper;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Year;
import me.francescotonini.univrorari.viewmodels.CoursesViewModel;

/**
 * Activity for R.layout.activity_select_course
 */
public class SelectCourseActivity extends BaseActivity implements CoursesAdapter.OnItemClickListener {
    @Override protected int getLayoutId() {
        return R.layout.activity_select_course;
    }

    @Override protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);

        // Show back button if required
        if (getIntent().getBooleanExtra("showBackButton", false)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override protected CoursesViewModel getViewModel() {
        if (viewModel == null) {
            CoursesViewModel.Factory factory = new CoursesViewModel.Factory(getApplication(), ((UniVROrariApp)getApplication()).getDataRepository().getCoursesRepository());
            viewModel = ViewModelProviders.of(this, factory).get(CoursesViewModel.class);
        }

        return viewModel;
    }

    @Override protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.activitySelectCoursesRefreshlayout.setRefreshing(true);
        getViewModel().getCourses().observe(this, (courses -> {
            if (courses == null) {
                Logger.e(SelectCourseActivity.class.getSimpleName(), "Got a NULL object");

                SnackBarHelper.show(binding.activitySelectCoursesRecyclerview, R.string.error_generic_message);

                return;
            }
            else if (courses.size() == 0) {
                Logger.w(SelectCourseActivity.class.getSimpleName(), "Got an empy list, is it correct?");

                return;
            }

            Logger.i(SelectCourseActivity.class.getSimpleName(), String.format("Got %s courses", courses.size()));

            // TODO: this workaround is to prevent data incosistency
            // https://github.com/thoughtbot/expandable-recycler-view/issues/147
            binding.activitySelectCoursesRecyclerview.setAdapter(new CoursesAdapter(courses, this));
            binding.activitySelectCoursesRefreshlayout.setEnabled(false);
            binding.activitySelectCoursesRefreshlayout.setRefreshing(false);
        }));
    }

    @Override public void onItemClick(Course course, Year year) {
        getViewModel().setCourse(course.getAcademicYearId(), year.getId(), course.getId());

        // Navigate to main (not the ideal solution)
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private ActivitySelectCourseBinding binding;
    private CoursesViewModel viewModel;
}
