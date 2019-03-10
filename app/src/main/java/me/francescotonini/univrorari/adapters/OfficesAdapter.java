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
import java.util.ArrayList;
import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ItemOfficeBinding;
import me.francescotonini.univrorari.models.Office;

/**
 * Adapter for a list of {@link Office}
 */
public class OfficesAdapter extends RecyclerView.Adapter<OfficesAdapter.ViewHolder> {
    /**
     * Initializes a new instance of this adapter
     * @param offices list of {@link Office}
     */
    public OfficesAdapter(List<Office> offices) {
        this.offices = offices;
        this.selectedOffices = new ArrayList<>();
    }

    /**
     * Gets the list of {@link Office} selected
     * @return list of {@link Office} selected
     */
    public List<Office> getSelectedOffices() {
        return selectedOffices;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOfficeBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_office,
                parent, false
        );

        return new ViewHolder(binding.getRoot());
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.set(offices.get(position));
    }

    @Override public int getItemCount() {
        return offices.size();
    }

    /**
     * ViewHolder for this adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Initializes a new instance of this view holder
         * @param itemView view
         */
        public ViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.getRoot().setOnClickListener(this);
            binding.itemOfficeCheck.setOnClickListener(this);
        }

        /**
         * Sets the office to show
         * @param office office
         */
        public void set(Office office) {
            this.office = office;

            binding.itemOfficeText.setText(this.office.getName());
            binding.itemOfficeCheck.setChecked(selectedOffices.contains(office));
        }

        @Override public void onClick(View v) {
            if (selectedOffices.contains(office)) {
                // Deselect
                binding.itemOfficeCheck.setChecked(false);
                selectedOffices.remove(office);
            }
            else {
                // Select
                binding.itemOfficeCheck.setChecked(true);
                selectedOffices.add(office);
            }
        }

        private Office office;
        private ItemOfficeBinding binding;
    }

    private List<Office> offices;
    private List<Office> selectedOffices;
}