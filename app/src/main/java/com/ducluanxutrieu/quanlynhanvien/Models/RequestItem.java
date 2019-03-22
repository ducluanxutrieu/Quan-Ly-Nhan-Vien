package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class RequestItem implements Serializable {
    private String date;
    private String name;
    private String content;
    private String time;
    private boolean accept;
    private String requestKey;
    private String uid;

    public RequestItem() {
    }

    public RequestItem(String date, String name, String uid, String content, String time, boolean accept) {
        this.date = date;
        this.name = name;
        this.uid = uid;
        this.content = content;
        this.accept = accept;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
