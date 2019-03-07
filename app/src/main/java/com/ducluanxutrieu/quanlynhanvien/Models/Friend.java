package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class Friend implements Serializable {
    String name;
    String email;
    String uid;
    String recentMessage;

    public Friend() {
    }

    public Friend(String name, String email, String uid, String recentMessage) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.recentMessage = recentMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

}
