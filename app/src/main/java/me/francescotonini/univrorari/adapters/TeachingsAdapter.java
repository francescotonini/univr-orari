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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ItemTeachingBinding;
import me.francescotonini.univrorari.models.Office;
import me.francescotonini.univrorari.models.Teaching;
import me.francescotonini.univrorari.models.Year;

/**
 * Adapter for a list of {@link Office}
 */
public class TeachingsAdapter extends RecyclerView.Adapter<TeachingsAdapter.ViewHolder> {
    /**
     * Initializes a new instance of this adapter
     * @param teachings list of {@link Teaching}
     * @param selectedYears list of {@link Year} already selected
     */
    public TeachingsAdapter(List<Teaching> teachings, List<Year> selectedYears) {
        this.teachings = teachings;
        this.selectedTeachings = new ArrayList<>();

        // TODO: find better solution
        for (Year year : selectedYears) {
            for (Teaching teaching : teachings) {
                if (teaching.getYearId().hashCode() == year.getId().hashCode()) {
                    this.selectedTeachings.add(teaching);
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            teachings.sort((t1, t2) -> {
                if (selectedTeachings.contains(t1) && selectedYears.contains(t2)) {
                    return 0;
                }
                else if (selectedTeachings.contains(t1)) {
                    return -1;
                }
                else if (selectedTeachings.contains(t2)) {
                    return 1;
                }

                return 0;
            });
        }
    }

    /**
     * Gets the list of {@link Teaching} selected
     * @return list of {@link Teaching} selected
     */
    public List<Teaching> getSelectedTeachings() {
        return selectedTeachings;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTeachingBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_teaching,
                parent, false
        );

        return new ViewHolder(binding.getRoot());
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.set(teachings.get(position));
    }

    @Override public int getItemCount() {
        return teachings.size();
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
            binding.itemTeachingCheck.setOnClickListener(this);
        }

        /**
         * Sets the teaching to show
         * @param teaching teaching
         */
        public void set(Teaching teaching) {
            this.teaching = teaching;

            binding.itemTeachingText.setText(this.teaching.getName());
            binding.itemTeachingCheck.setChecked(selectedTeachings.contains(this.teaching));
        }

        @Override public void onClick(View v) {
            if (selectedTeachings.contains(this.teaching)) {
                // Deselect
                binding.itemTeachingCheck.setChecked(false);
                selectedTeachings.remove(this.teaching);
            }
            else {
                // Select
                binding.itemTeachingCheck.setChecked(true);
                selectedTeachings.add(this.teaching);
            }
        }

        private Teaching teaching;
        private ItemTeachingBinding binding;
    }

    private List<Teaching> selectedTeachings;
    private List<Teaching> teachings;
}