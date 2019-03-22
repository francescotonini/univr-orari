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

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import com.google.gson.Gson;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.UniVROrariApp;
import it.francescotonini.univrorari.adapters.RoomsAdapter;
import it.francescotonini.univrorari.databinding.ActivityRoomsBinding;
import it.francescotonini.univrorari.helpers.DialogHelper;
import it.francescotonini.univrorari.models.ApiResponse;
import it.francescotonini.univrorari.models.Room;
import it.francescotonini.univrorari.viewmodels.RoomsViewModel;

public class RoomsActivity extends BaseActivity implements RoomsAdapter.OnItemClickListener, Observer<ApiResponse<List<Room>>>, SearchView.OnQueryTextListener {
    @Override protected int getLayoutId() {
        return R.layout.activity_rooms;
    }

    @Override protected RoomsViewModel getViewModel() {
        if (viewModel == null) {
            RoomsViewModel.Factory factory = new RoomsViewModel.Factory(getApplication(), ((UniVROrariApp)getApplication()).getDataRepository().getRoomsRepository());
            viewModel = ViewModelProviders.of(this, factory).get(RoomsViewModel.class);
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

        // Setup recyclerview
        adapter = new RoomsAdapter(this);
        binding.activityRoomsRecyclerView.setAdapter(adapter);

        // Setup refresh event
        binding.activityRoomsRefreshlayout.setOnRefreshListener(() -> {
            getViewModel().refresh();
        });

        // Start animation
        binding.activityRoomsRefreshlayout.setRefreshing(true);

        // Connect livedata
        getViewModel().getRooms().observe(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handles toolbar's back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onItemClick(Room room) {
        Intent intent = new Intent(this, RoomDetailsActivity.class);
        intent.putExtra("room", (new Gson()).toJson(room));
        startActivity(intent);
    }

    @Override public void onChanged(@Nullable ApiResponse<List<Room>> rooms) {
        if (!rooms.isSuccessful()) {
            // Show error messages and stop activity
            DialogHelper.show(this, R.string.error_network_title, R.string.error_network_message, R.string.error_network_button_message);

            return;
        }

        // Update data shown
        adapter.update(rooms.getData());

        // Update UI accordingly
        binding.activityRoomsRefreshlayout.setRefreshing(false);
    }

    @Override public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override public boolean onQueryTextChange(String s) {
        adapter.getFilter().filter(s);
        return true;
    }

    private RoomsAdapter adapter;
    private RoomsViewModel viewModel;
    private ActivityRoomsBinding binding;
}
