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

package me.francescotonini.univrorari.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ItemRoomBinding;
import me.francescotonini.univrorari.helpers.DateToStringFormatter;
import me.francescotonini.univrorari.models.Room;

/**
 * Adapter for a list of {@link Room}
 */
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    /**
     * Initializes a new instance of this adapter
     */
    public RoomsAdapter(OnItemClickListener listener) {
        this.queryValue = "";
        this.listener = listener;
        this.rooms = new ArrayList<>();
    }

    /**
     * Update the list of {@link Room} shown
     * @param rooms list of {@link Room}
     */
    public void update(List<Room> rooms) {
        this.rooms = rooms;
        Collections.sort(this.rooms, ((o1, o2) -> o1.getName().compareTo(o2.getName())));

        this.notifyDataSetChanged();
    }

    /**
     * Item click interface
     */
    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoomBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_room,
                parent, false
        );

        return new ViewHolder(binding.getRoot());
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.set(getRooms().get(position));
    }

    @Override public int getItemCount() {
        return getRooms().size();
    }

    /**
     * Gets the filter of this list
     * @return filter
     */
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                queryValue = constraint.toString();
                return null;
            }

            @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    private List<Room> getRooms() {
        if (queryValue.isEmpty()) {
            return rooms;
        }

        List<Room> filteredRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getName().toLowerCase().contains(queryValue.toLowerCase())) {
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }

    /**
     * View holder for this adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Initializes a new instance of this View holder
         * @param itemView view
         */
        public ViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        /**
         * Sets the room to show
         * @param room room
         */
        public void set(Room room) {
            this.room = room;

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(room));
            binding.itemRoomText.setText(this.room.getName());
            binding.itemRoomOfficeText.setText(this.room.getOfficeName());

            Room.Event nowEvent = this.room.getCurrentEvent();
            Room.Event nextEvent = this.room.getNextEvent();

            if (nowEvent != null) {
                binding.itemRoomTimeDescriptionText.setText(R.string.item_room_available_from);
                binding.itemRoomEtaDescriptionText.setText(R.string.item_room_busy_for);
                binding.itemRoomTimeText.setText(DateToStringFormatter.getTimeString(nowEvent.getEndTimestamp()));
                binding.itemRoomEtaText.setText(DateToStringFormatter.getETAString(nowEvent.getEndTimestamp()));

                binding.itemRoomTopRelativelayout.setBackgroundResource(R.color.dark_red);
                binding.itemRoomBottomLinearlayout.setBackgroundResource(R.color.red);
            }
            else if (nextEvent != null) {
                binding.itemRoomTimeDescriptionText.setText(R.string.item_room_busy_from);
                binding.itemRoomEtaDescriptionText.setText(R.string.item_room_available_for);
                binding.itemRoomTimeText.setText(DateToStringFormatter.getTimeString(nextEvent.getStartTimestamp()));
                binding.itemRoomEtaText.setText(DateToStringFormatter.getETAString(nextEvent.getStartTimestamp()));

                binding.itemRoomTopRelativelayout.setBackgroundResource(R.color.dark_green);
                binding.itemRoomBottomLinearlayout.setBackgroundResource(R.color.green);
            }
            else {
                Logger.e(RoomsAdapter.class.getSimpleName(), "Both nextEvent and nowEvent are null.");
            }
        }

        private Room room;
        private ItemRoomBinding binding;
    }

    private OnItemClickListener listener;
    private String queryValue;
    private List<Room> rooms;
}
