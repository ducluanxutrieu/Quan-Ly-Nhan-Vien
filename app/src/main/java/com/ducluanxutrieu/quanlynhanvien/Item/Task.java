package com.ducluanxutrieu.quanlynhanvien.Item;

import java.io.Serializable;

public class Task implements Serializable {
    private String taskContent;
    private String taskTitle;

    public Task() {
    }

    public Task(String taskContent, String taskTitle)  {
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
}
