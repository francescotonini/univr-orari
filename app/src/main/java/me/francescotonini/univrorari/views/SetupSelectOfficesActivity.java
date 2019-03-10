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

import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.OfficesAdapter;
import me.francescotonini.univrorari.adapters.TeachingsAdapter;
import me.francescotonini.univrorari.databinding.ActivitySetupSelectOfficesBinding;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.helpers.SimpleDividerItemDecoration;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.models.Teaching;
import me.francescotonini.univrorari.viewmodels.OfficesViewModel;

/**
 * Activity for R.layout.activity_select_course
 */
public class SetupSelectOfficesActivity extends BaseActivity {
    @Override protected int getLayoutId() {
        return R.layout.activity_setup_select_offices;
    }

    @Override protected OfficesViewModel getViewModel() {
        if (viewModel == null) {
            OfficesViewModel.Factory factory = new OfficesViewModel.Factory(getApplication(),
                    ((UniVROrariApp)getApplication()).getDataRepository().getOfficesRepository());
            viewModel = ViewModelProviders.of(this, factory).get(OfficesViewModel.class);
        }

        return viewModel;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        // Setup Toolbar
        setSupportActionBar((Toolbar)binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.activity_setup_select_offices_description);

        binding.activitySelectOfficesSaveButton.setEnabled(false);

        // Add item decoration to RecyclerView
        binding.activitySelectOfficesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplication().getApplicationContext(), this.getResources().getColor(R.color.divider), 3));
        binding.activitySelectOfficesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start progress bar
        binding.activitySelectOfficesRefreshlayout.setRefreshing(true);
        binding.activitySelectOfficesRefreshlayout.setEnabled(true);

        // Click listener for the save button
        binding.activitySelectOfficesSaveButton.setOnClickListener(saveButtonClickListener);

        // Get list of offices to show + start animation
        binding.activitySelectOfficesRefreshlayout.setRefreshing(true);
        getViewModel().getOffices().observe(this, officesObserver);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private ActivitySetupSelectOfficesBinding binding;
    private OfficesViewModel viewModel;

    private View.OnClickListener saveButtonClickListener = click -> {
        List<Office> selectedOffices = ((OfficesAdapter)binding.activitySelectOfficesRecyclerView.getAdapter()).getSelectedOffices();
        getViewModel().savePreferences(selectedOffices);

        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
    };

    private Observer<ApiResponse<List<Office>>> officesObserver = offices -> {
        if (!offices.isSuccessful()) {
            DialogHelper.show(this, R.string.error_network_title, R.string.error_network_message, R.string.error_network_button_message);

            binding.activitySelectOfficesRefreshlayout.setRefreshing(false);
            binding.activitySelectOfficesRefreshlayout.setEnabled(false);

            onBackPressed();
            return;
        }

        binding.activitySelectOfficesRecyclerView.setAdapter(new OfficesAdapter(offices.getData()));
        binding.activitySelectOfficesSaveButton.setEnabled(true);
        binding.activitySelectOfficesRefreshlayout.setRefreshing(false);
        binding.activitySelectOfficesRefreshlayout.setEnabled(false);
    };
}