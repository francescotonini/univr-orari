package me.francescotonini.univrorari.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import me.francescotonini.univrorari.Logger;
import me.francescotonini.univrorari.viewmodels.BaseViewModel;

/**
 * Standard Activity skeleton
 */
public abstract class BaseActivity extends AppCompatActivity {
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Why? An Activity may not have a layout. If that is the case, layoutId is zero
        if (getLayoutId() == 0) {
            Logger.w(BaseActivity.class.getSimpleName(), "Layout id is zero");

            return;
        }
    }
}
