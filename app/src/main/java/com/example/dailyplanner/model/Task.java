package com.example.dailyplanner.model;

import java.io.Serializable;
import java.util.UUID;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";

    private String id;
    private String title;
    private String description;
    private String time;
    private boolean isCompleted;
    private String priority; // HIGH, MEDIUM, LOW

    public Task(String title, String description, String time) {
        this(title, description, time, PRIORITY_MEDIUM);
    }

    public Task(String title, String description, String time, String priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.time = time;
        this.isCompleted = false;
        this.priority = priority != null ? priority : PRIORITY_MEDIUM;
    }

    public Task(String id, String title, String description, String time, boolean isCompleted, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isCompleted = isCompleted;
        this.priority = priority != null ? priority : PRIORITY_MEDIUM;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getPriority() {
        return priority != null ? priority : PRIORITY_MEDIUM;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}