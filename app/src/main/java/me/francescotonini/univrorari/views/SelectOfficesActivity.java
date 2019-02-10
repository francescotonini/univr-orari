package me.francescotonini.univrorari.views;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.OfficesAdapter;
import me.francescotonini.univrorari.databinding.ActivitySelectOfficesBinding;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.helpers.SimpleDividerItemDecoration;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.viewmodels.OfficesViewModel;

/**
 * Activity for R.layout.activity_select_course
 */
public class SelectOfficesActivity extends BaseActivity {
    @Override protected int getLayoutId() {
        return R.layout.activity_select_offices;
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

        // Add item decoration to RecyclerView
        binding.activitySelectOfficesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplication().getApplicationContext(), this.getResources().getColor(R.color.divider), 3));

        // Click listener for the save button
        binding.activitySelectOfficesSaveButton.setEnabled(false);
        binding.activitySelectOfficesSaveButton.setOnClickListener(click -> {
            List<Office> selectedOffices = ((OfficesAdapter)binding.activitySelectOfficesRecyclerView.getAdapter())
                    .getSelectedOffices();
            getViewModel().setOffices(selectedOffices);

            // Once saved, go back
            onBackPressed();
        });

        // Get list of offices to show + start animation
        binding.activitySelectOfficesRefreshlayout.setRefreshing(true);
        getViewModel().getOffices().observe(this, offices -> {
            // if offices is null; it means something went wrong while I was trying to download the list of offices from the API
            // could be missing network, offline APIs or wrong deserialization.
            if (!offices.isSuccessful()) {
                DialogHelper.show(this, R.string.error_network_title, R.string.error_network_message, R.string.error_network_button_message);

                binding.activitySelectOfficesCenterText.setText(R.string.activity_select_offices_loading_error);
                binding.activitySelectOfficesRefreshlayout.setRefreshing(false);

                return;
            }

            binding.activitySelectOfficesRecyclerView.setAdapter(new OfficesAdapter(offices.getData()));

            // Update UI accordingly
            binding.activitySelectOfficesCenterText.setVisibility(View.INVISIBLE);
            binding.activitySelectOfficesSaveButton.setEnabled(true);
            binding.activitySelectOfficesRefreshlayout.setRefreshing(false);
            binding.activitySelectOfficesRefreshlayout.setEnabled(true);
        });
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private ActivitySelectOfficesBinding binding;
    private OfficesViewModel viewModel;
}
