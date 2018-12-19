package me.francescotonini.univrorari.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Implementation of {@link Year} for api purposes
 */
public class Year implements Parcelable {
    // Why this class implements Parcelable? ExpandableRecyclerViewAdapter (see CoursesAdapter)
    // requires that child class must inherit Parcelable

    protected Year(Parcel in) {
        name = in.readString();
        id = in.readString();
    }

    /**
     * Gets the name of this {@link Year}
     * @return name of this {@link Year}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this {@link Year}
     * @param name name of this {@link Year}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id of this {@link Year}
     * @return id of this {@link Year}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this {@link Year}
     * @param id id of this {@link Year}
     */
    public void setId(String id) {
        this.id = id;
    }

    // The following 3 functions are the implementation of Parcelable
    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
    }

    public static final Creator<Year> CREATOR = new Creator<Year>() {
        @Override
        public Year createFromParcel(Parcel in) {
            return new Year(in);
        }

        @Override
        public Year[] newArray(int size) {
            return new Year[size];
        }
    };

    private String name;
    private String id;
}
