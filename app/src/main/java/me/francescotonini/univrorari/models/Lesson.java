package me.francescotonini.univrorari.models;

/**
 * Implementation of {@link Lesson} for db purposes
 */
public class Lesson {
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

    private String name;
    private String teacher;
    private String room;
    private long startTimestamp;
    private long endTimestamp;
}

