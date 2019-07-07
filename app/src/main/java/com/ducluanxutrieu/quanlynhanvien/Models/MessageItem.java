package com.ducluanxutrieu.quanlynhanvien.Models;

public class MessageItem {
    private String text;
    private String email;
    private String name;
    private String uid;
    private String picture;
    private String timeStamp;

    public MessageItem() {
    }

    public MessageItem(String text, String email, String name, String uid, String picture, String timeStamp) {
        this.text = text;
        this.email = email;
        this.name = name;
        this.uid = uid;
        this.picture = picture;
        this.timeStamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "MessageItem{" +
                "text='" + text + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", picture='" + picture + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}

