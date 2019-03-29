package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class Tasks implements Serializable {
    private String taskContent;
    private String taskTitle;
    private String keyTask;

    public Tasks() {
    }

    public Tasks(String taskTitle, String taskContent)  {
        this.taskContent = taskContent;
        this.taskTitle = taskTitle;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getKeyTask() {
        return keyTask;
    }

    public void setKeyTask(String keyTask) {
        this.keyTask = keyTask;
    }
}
