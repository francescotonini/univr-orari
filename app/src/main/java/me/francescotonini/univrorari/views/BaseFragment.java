package me.francescotonini.univrorari.views;

import android.support.v4.app.Fragment;

import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

/**
 * Fragment skeleton
 */
public abstract class BaseFragment extends Fragment {
    /**
     * Gets the layout id
     * @return The layout id
     */
    protected abstract int getLayoutId();

    /**
     * Gets the view model
     * @return view model
     */
    protected abstract BaseViewModel getViewModel();

    /**
     * Refreshes the content of the fragment
     */
    public void refresh() { ; }
}
