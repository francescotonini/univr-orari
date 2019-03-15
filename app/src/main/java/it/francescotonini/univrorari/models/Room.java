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

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import it.francescotonini.univrorari.R;

/**
 * Represent a room inside an office
 */
public class Room {
    /**
     * Represents an event inside a {@link Room}
     */
    public class Event implements WeekViewDisplayable<Event>
    {
        /**
         * Gets the name of the event
         * @return name of the event
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the event
         * @param name name of the event
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the start timestamp of the event
         * @return start timestamp of the event
         */
        public long getStartTimestamp() {
            return startTimestamp * 1000;
        }

        /**
         * Sets the start timestamp of the event
         * @param startTimestamp start timestamp of the event
         */
        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        /**
         * Gets the end timestamp of the event
         * @return end timestamp of the event
         */
        public long getEndTimestamp() {
            return endTimestamp * 1000;
        }

        /**
         * Sets the end timestamp of the event
         * @param endTimestamp end timestamp of the event
         */
        public void setEndTimestamp(long endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        @Override public WeekViewEvent<Event> toWeekViewEvent() {
            Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            start.setTimeInMillis(getStartTimestamp());
            Calendar end = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            end.setTimeInMillis(getEndTimestamp());
            end.set(Calendar.MINUTE, end.get(Calendar.MINUTE) - 1);

            return new WeekViewEvent<>(getStartTimestamp(), getName(), start, end, "", 0, false, null);
        }

        private String name;
        private long startTimestamp;
        private long endTimestamp;
    }

    /**
     * Gets the name of the room
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a list of {@link Event}
     * @return a list of {@link Event}
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the name of this room
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets a list of {@link Event}
     * @param events list of {@link Event}
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Gets the name of the {@link Office} associated with this room
     * @return name of the {@link Office}
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * Sets the name of the {@link Office} associated with this {@link Room}
     * @param officeName name of the {@link Office}
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * Gets the id of the {@link Room}
     * @return {@link Room} id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the {@link Room}
     * @param id id of the {@link Room}
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets a boolean indicating whether or not this room is available or not
     * @return if TRUE this room is available; otherwise not
     */
    public boolean isFree() {
        return isFree;
    }

    /**
     * Sets a boolean indicating whether or not this room is available or not
     * @param free if TRUE this room is available; otherwise not
     */
    public void setFree(boolean free) {
        isFree = free;
    }

    /**
     * Gets the timestamp when the room gets available or not
     * @return the timestamp when the room gets available or not
     */
    public long getUntil() {
        return until;
    }

    /**
     * Sets the timestamp when the room gets available or not
     * @param until the timestamp when the room gets available or not
     */
    public void setUntil(long until) {
        this.until = until;
    }

    private int id;
    private String officeName;
    private String name;
    private List<Event> events;
    private boolean isFree;
    private long until;
}
