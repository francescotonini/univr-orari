package me.francescotonini.univrorari.models;

/**
 * Represents an office (every office has one or more rooms - e.g. Ca' Vignal 2)
 */
public class Office {
    /**
     * Gets the name of the office
     * @return gets name of the office
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the id of the office
     * @return the id of the office
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the id
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    private String name;
    private String id;
}
