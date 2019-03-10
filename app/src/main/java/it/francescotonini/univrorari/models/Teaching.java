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

/**
 * Represents a subject (e.g. Calculus)
 */
public class Teaching {
    /**
     * Gets the name of this {@link Teaching}
     * @return name of this {@link Teaching}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this {@link Teaching}
     * @param name name of this {@link Teaching}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id of this {@link Teaching}
     * @return id of this {@link Teaching}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this {@link Teaching}
     * @param id id of this {@link Teaching}
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the year id of this {@link Teaching}
     * @return year id of this {@link Teaching}
     */
    public String getYearId() {
        return yearId;
    }

    /**
     * Sets the year id of this {@link Teaching}
     * @param yearId year id of this {@link Teaching}
     */
    public void setYearId(String yearId) {
        this.yearId = yearId;
    }

    private String name;
    private String id;
    private String yearId;
}
