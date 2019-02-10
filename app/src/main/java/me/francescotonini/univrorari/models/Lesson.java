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

    private String name;
    private String teacher;
    private String room;
    private long startTimestamp;
    private long endTimestamp;
}

