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

package it.francescotonini.univrorari.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import it.francescotonini.univrorari.Logger;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.databinding.ItemRoomBinding;
import it.francescotonini.univrorari.helpers.DateToStringFormatter;
import it.francescotonini.univrorari.models.Room;

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
         *
         * @param itemView view
         */
        public ViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        /**
         * Sets the room to show
         *
         * @param room room
         */
        public void set(Room room) {
            this.room = room;

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(room));
            binding.itemRoomText.setText(this.room.getName());
            binding.itemRoomOfficeText.setText(this.room.getOfficeName());

            if (!room.isFree()) {
                binding.itemRoomTimeText.setText(DateToStringFormatter.getTimeString(room.getUntil()));
                binding.itemRoomTopRelativelayout.setBackgroundResource(R.color.red);
            } else {
                binding.itemRoomTimeText.setText(DateToStringFormatter.getTimeString(room.getUntil()));
                binding.itemRoomTopRelativelayout.setBackgroundResource(R.color.green);
            }
        }

        private Room room;
        private ItemRoomBinding binding;
    }

    private OnItemClickListener listener;
    private String queryValue;
    private List<Room> rooms;
}
