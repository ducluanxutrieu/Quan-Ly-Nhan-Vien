package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class Friend implements Serializable {
    private String name;
    private String uid;
    private String recentMessage;
    private String avatarUrl;

    public Friend() {
    }

    public Friend(String name, String uid, String recentMessage, String avatarUrl) {
        this.name = name;
        this.uid = uid;
        this.recentMessage = recentMessage;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
