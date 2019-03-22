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

package it.francescotonini.univrorari.models;

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
