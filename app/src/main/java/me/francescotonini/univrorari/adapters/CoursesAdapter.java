package me.francescotonini.univrorari.adapters;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.francescotonini.univrorari.R;
import me.francescotonini.univrorari.databinding.ItemCourseBinding;
import me.francescotonini.univrorari.databinding.ItemYearBinding;
import me.francescotonini.univrorari.models.Course;
import me.francescotonini.univrorari.models.Year;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * {@link ExpandableRecyclerViewAdapter} that can display a list of {@link Course} and {@link Year}
 */
public class CoursesAdapter extends
        ExpandableRecyclerViewAdapter<CoursesAdapter.CourseViewHolder, CoursesAdapter.YearViewHolder> {
    /**
     * Initializes a new instance of {@link CoursesAdapter}
     * @param courses a list of courses
     */
    public CoursesAdapter(List<Course> courses, OnItemClickListener listener) {
        super(toListExpandable(courses));

        this.listener = listener;
    }

    @Override public CourseViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        ItemCourseBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_course,
                parent, false
        );

        return new CourseViewHolder(binding.getRoot());
    }

    @Override public YearViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        ItemYearBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_year,
                parent, false
        );

        return new YearViewHolder(binding.getRoot());
    }

    @Override public void onBindChildViewHolder(YearViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        holder.bind(((Expandable)group).getCourse(), ((Expandable)group).getCourse().getYears().get(childIndex));
    }

    @Override public void onBindGroupViewHolder(CourseViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind(((Expandable)group).getCourse());
    }

    /**
     * Group ViewHolder for {@link CoursesAdapter}
     */
    public class CourseViewHolder extends GroupViewHolder {
        /**
         * Initializes a new instance of this view holder
         * @param view view
         */
        public CourseViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        /**
         * Binds a course to this view
         * @param course course
         */
        public void bind(Course course) {
            binding.itemCourseText.setText(course.getName());
        }

        @Override public void expand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            binding.itemCourseArrow.setAnimation(rotate);
        }

        @Override public void collapse() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            binding.itemCourseArrow.setAnimation(rotate);
        }

        private ItemCourseBinding binding;
    }

    /**
     * Child ViewHolder of {@link CourseViewHolder} for {@link CoursesAdapter}
     */
    public class YearViewHolder extends ChildViewHolder {
        /**
         * Initializes a new instance of this view holder
         * @param view view
         */
        public YearViewHolder(View view) {
            super(view);
            this.view = view;

            binding = DataBindingUtil.bind(view);
        }

        /**
         * Binds a year to this view
         * @param course course
         * @param year year
         */
        public void bind(Course course, Year year) {
            binding.itemYearText.setText(year.getName());

            view.setOnClickListener(view -> listener.onItemClick(course, year));
        }

        private View view;
        private ItemYearBinding binding;
    }

    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(Course course, Year year);
    }

    private OnItemClickListener listener;

    // Converts a Course in a "expandable" Course
    private static List<Expandable> toListExpandable(List<Course> courses) {
        List<Expandable> result = new ArrayList<>();
        for (Course c: courses) {
            result.add(new Expandable(c));
        }

        return result;
    }

    // This class "converts" a Course in a "expandable" Course. In other words
    // it's just an helper class for this adapter
    private static class Expandable extends ExpandableGroup<Year> {
        public Expandable(Course c) {
            super(c.getName(), c.getYears());

            course = c;
        }

        public Course getCourse() {
            return course;
        }

        private Course course;
    }
}

