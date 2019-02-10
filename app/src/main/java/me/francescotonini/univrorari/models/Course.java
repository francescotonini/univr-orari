package me.francescotonini.univrorari.models;

import java.util.List;

/**
 * Represents a university course (e.g. "Computer Science")
 */
public class Course {
    /**
     * Gets the name of this {@link Course}
     * @return name of this {@link Course}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this {@link Course}
     * @param name name of this {@link Course}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id of this academic year
     * @return the id of this academic year
     */
    public String getAcademicYearId() {
        return academicYearId;
    }

    /**
     * Sets the id of this academic year
     * @param academicYearId id of this academic year
     */
    public void setAcademicYearId(String academicYearId) {
        this.academicYearId = academicYearId;
    }

    /**
     * Gets the id of this {@link Course}
     * @return the id of this {@link Course}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this {@link Course}
     * @param id id of this {@link Course}
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets a list of {@link Year}
     * @return a list of {@link Year}
     */
    public List<Year> getYears() {
        return years;
    }

    /**
     * Sets a list of {@link Year}
     * @param years a list of {@link Year}
     */
    public void setYear(List<Year> years) {
        this.years = years;
    }

    private String name;
    private String id;
    private String academicYearId;
    private List<Year> years;
}
