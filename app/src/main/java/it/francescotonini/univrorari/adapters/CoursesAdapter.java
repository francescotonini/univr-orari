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

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;
import it.francescotonini.univrorari.R;
import it.francescotonini.univrorari.databinding.ItemCourseBinding;
import it.francescotonini.univrorari.models.Course;

/**
 * Adapter that can display a list of {@link Course}
 */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {
    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    /**
     * Initializes a new instance of {@link CoursesAdapter}
     * @param courses a list of courses
     */
    public CoursesAdapter(List<Course> courses, OnItemClickListener listener) {
        this.listener = listener;
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCourseBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_course,
                viewGroup, false
        );

        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getCourses().get(position));
    }

    @Override
    public int getItemCount() {
        return getCourses().size();
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

    /**
     * Group ViewHolder for {@link CoursesAdapter}
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Initializes a new instance of this view holder
         * @param view view
         */
        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        /**
         * Binds a course to this view
         * @param course course
         */
        public void bind(Course course) {
            binding.itemCourseText.setText(course.getName().replace("Laurea", "L."));
            binding.getRoot().setOnClickListener(view -> listener.onItemClick(course));
        }

        private final ItemCourseBinding binding;
    }

    private List<Course> getCourses() {
        if (queryValue == null || queryValue.isEmpty()) {
            return courses;
        }

        List<Course> filteredCourses = new ArrayList<>();
        for (Course room : courses) {
            if (room.getName().toLowerCase().contains(queryValue.toLowerCase())) {
                filteredCourses.add(room);
            }
        }

        return filteredCourses;
    }

    private String queryValue;
    private final OnItemClickListener listener;
    private final List<Course> courses;
}

