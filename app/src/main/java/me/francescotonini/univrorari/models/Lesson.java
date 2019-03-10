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

package me.francescotonini.univrorari.models;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Represents a lesson of a university course
 */
public class Lesson implements WeekViewDisplayable<Lesson> {
    /**
     * Gets the id of this {@link Lesson}
     * @return id of this {@link Lesson}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this {@link Lesson}
     * @param id id of this {@link Lesson}
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of this {@link Lesson}
     * @return name of this {@link Lesson}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this {@link Lesson}
     * @param name name of this {@link Lesson}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this {@link Lesson} teacher
     * @return name of this {@link Lesson} teacher
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * Sets the name of this {@link Lesson} teacher
     * @param name name of this {@link Lesson} teacher
     */
    public void setTeacher(String name) {
        this.teacher = name;
    }

    /**
     * Gets the room of this {@link Lesson}
     * @return room of this {@link Lesson}
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the room of this {@link Lesson}
     * @param room room of this {@link Lesson}
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Gets the start timestamp of this {@link Lesson}
     * @return the start timestamp of this {@link Lesson}
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Sets the start timestamp of this {@link Lesson}
     * @param startTimestamp start timestamp of this {@link Lesson}
     */
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Gets the end timestamp of this {@link Lesson}
     * @return the end timestamp of this {@link Lesson}
     */
    public long getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * Sets the end timestamp of this {@link Lesson}
     * @param endTimestamp end timestamp of this {@link Lesson}
     */
    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    @Override public WeekViewEvent<Lesson> toWeekViewEvent() {
        Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        startTime.setTimeInMillis(startTimestamp);
        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        endTime.setTimeInMillis(endTimestamp);
        endTime.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE) - 1);

        return new WeekViewEvent<>(startTimestamp, name, startTime, endTime, room, 0, false, this);
    }

    private String id;
    private String yearId;
    private String name;
    private String teacher;
    private String room;
    private long startTimestamp;
    private long endTimestamp;
}

