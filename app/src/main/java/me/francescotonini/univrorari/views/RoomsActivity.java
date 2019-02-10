package me.francescotonini.univrorari.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.gson.Gson;
import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.UniVROrariApp;
import me.francescotonini.univrorari.adapters.RoomsAdapter;
import me.francescotonini.univrorari.databinding.ActivityRoomsBinding;
import me.francescotonini.univrorari.helpers.DialogHelper;
import me.francescotonini.univrorari.helpers.PreferenceHelper;
import me.francescotonini.univrorari.models.ApiResponse;
import me.francescotonini.univrorari.models.Room;
import me.francescotonini.univrorari.viewmodels.RoomsViewModel;

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

        // Setup livedata
        getViewModel().getRooms().observe(this, this);

        // If first boot, go to SelectOfficesActivity
        if (!PreferenceHelper.getBoolean(PreferenceHelper.Keys.ROOMS_DID_FIRST_START)) {
            startActivity(new Intent(this, SelectOfficesActivity.class));
        }

        // Start animation
        binding.activityRoomsRefreshlayout.setRefreshing(true);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rooms, menu);

        MenuItem search = menu.findItem(R.id.menu_rooms_search);
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_rooms_change_offices) {
            startActivity(new Intent(this, SelectOfficesActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onItemClick(Room room) {
        Intent intent = new Intent(this, RoomDetailsActivity.class);
        intent.putExtra("room", (new Gson()).toJson(room));
        startActivity(intent);
    }

    @Override public void onChanged(@Nullable ApiResponse<List<Room>> rooms) {
        // if rooms is null; it means something went wrong while I was trying to download the list of offices from the API
        // could be missing network, offline APIs or wrong deserialization.
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
