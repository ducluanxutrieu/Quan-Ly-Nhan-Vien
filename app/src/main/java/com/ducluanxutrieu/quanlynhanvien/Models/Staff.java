package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class Staff implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String position;
    private String uid;
    private boolean admin;
    private String avatarUrl;
    private String password;

    public Staff(String name, String email, String phone, String position, String uid, String avatarUrl, String password, boolean admin) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.uid = uid;
        this.avatarUrl = avatarUrl;
        this.password = password;
        this.admin = admin;
    }

    public Staff() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
