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
import me.francescotonini.univrorari.databinding.ItemYearBinding;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.models.Year;

/**
 * Adapter for a list of {@link Office}
 */
public class YearsAdapter extends RecyclerView.Adapter<YearsAdapter.ViewHolder> {
    /**
     * Initializes a new instance of this adapter
     * @param years list of {@link Year}
     */
    public YearsAdapter(List<Year> years) {
        this.years = years;
        this.selectedYears = new ArrayList<>();
    }

    /**
     * Gets the list of {@link Year} selected
     * @return list of {@link Year} selected
     */
    public List<Year> getSelectedYears() {
        return selectedYears;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemYearBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_year,
                parent, false
        );

        return new ViewHolder(binding.getRoot());
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.set(years.get(position));
    }

    @Override public int getItemCount() {
        return years.size();
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
            binding.itemYearCheck.setOnClickListener(this);
        }

        /**
         * Sets the year to show
         * @param year year
         */
        public void set(Year year) {
            this.year = year;

            binding.itemYearText.setText(this.year.getName());
            binding.itemYearCheck.setChecked(selectedYears.contains(year));
        }

        @Override public void onClick(View v) {
            if (selectedYears.contains(year)) {
                // Deselect
                binding.itemYearCheck.setChecked(false);
                selectedYears.remove(year);
            }
            else {
                // Select
                binding.itemYearCheck.setChecked(true);
                selectedYears.add(year);
            }
        }

        private Year year;
        private ItemYearBinding binding;
    }

    private List<Year> years;
    private List<Year> selectedYears;
}