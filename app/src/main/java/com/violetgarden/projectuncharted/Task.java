package com.violetgarden.projectuncharted;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    public Task(String taskName,int timeRequired) {
        this.timeRequired = timeRequired;
        this.taskName = taskName;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int timeRequired;
    public String taskName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(int timeRequired) {
        this.timeRequired = timeRequired;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
