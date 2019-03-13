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

            return new WeekViewEvent<>(getStartTimestamp(), getName(), start, end, "", R.color.accent, false, null);
        }

        private String name;
        private String officeName;
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
     * Gets the current {@link Room.Event}
     * @return the current {@link Room.Event}
     */
    public Room.Event getCurrentEvent() {
        Date now = new Date();

        // Are you using the app after the uni closed?
        if (now.after(getClosingCalendar().getTime())) {
            return getCloseEvent();
        }

        // Are you here before opening time?
        if (now.before(getOpeningCalendar().getTime())) {
            Event e = new Room.Event();
            e.setStartTimestamp(now.getTime() / 1000L);
            e.setEndTimestamp(getOpeningCalendar().getTime().getTime() / 1000L);

            return e;
        }

        // Check for events
        for (Room.Event e : getEvents()) {
            Date startTime = new Date(e.getStartTimestamp());
            Date endTime = new Date(e.getEndTimestamp());

            if (startTime.before(now) && endTime.after(now)) {
                return e;
            }
        }

        return null;
    }

    private Calendar getClosingCalendar() {
        Calendar closingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        closingCalendar.set(Calendar.HOUR_OF_DAY, 19);
        closingCalendar.set(Calendar.MINUTE, 30);

        return closingCalendar;
    }

    private Calendar getOpeningCalendar() {
        Calendar openingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        openingCalendar.set(Calendar.HOUR_OF_DAY, 7);
        openingCalendar.set(Calendar.MINUTE, 30);

        return openingCalendar;
    }

    /**
     * Gets the next {@link Room.Event}
     * @return the next {@link Room.Event}
     */
    public Room.Event getNextEvent() {
        Date now = new Date();
        for (Room.Event e : getEvents()) {
            Date startTime = new Date(e.getStartTimestamp());

            if (startTime.after(now)) {
                return e;
            }
        }

        Calendar closingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        closingCalendar.set(Calendar.HOUR_OF_DAY, 19);
        closingCalendar.set(Calendar.MINUTE, 30);

        if (now.before(closingCalendar.getTime())) {
            return getCloseEvent();
        }

        return null;
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
     * Gets the event between closing time and opening time of this room
     * @return "room close" event
     */
    public Room.Event getCloseEvent() {
        Event event = new Event();
        event.name = "Aula chiusa."; // TODO: replace

        Calendar closingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        closingCalendar.set(Calendar.HOUR_OF_DAY, 19);
        closingCalendar.set(Calendar.MINUTE, 30);

        Calendar openingCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
        openingCalendar.set(Calendar.HOUR_OF_DAY, 7);
        openingCalendar.set(Calendar.MINUTE, 30);
        openingCalendar.set(Calendar.DAY_OF_MONTH, openingCalendar.get(Calendar.DAY_OF_MONTH) + 1);

        event.setStartTimestamp(closingCalendar.getTimeInMillis() / 1000L);
        event.setEndTimestamp(openingCalendar.getTimeInMillis() / 1000L);

        return event;
    }

    private int id;
    private String officeName;
    private String name;
    private List<Event> events;
}
