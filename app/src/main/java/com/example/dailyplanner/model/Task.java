package com.example.dailyplanner.model;

import java.io.Serializable;
import java.util.UUID;

public class Task implements Serializable {
    private String id;
    private String title;
    private String description;
    private String time;
    private boolean isCompleted;

    public Task(String title, String description, String time) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.time = time;
        this.isCompleted = false;
    }

    public Task(String id, String title, String description, String time, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isCompleted = isCompleted;
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
}
