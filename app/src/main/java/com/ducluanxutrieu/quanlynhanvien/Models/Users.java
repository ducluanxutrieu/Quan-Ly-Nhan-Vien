package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class Users implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String position;
    private String password;
    private String uid;
    private boolean admin;

    public Users(String name, String email, String password, String phone, String position, String uid, boolean admin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.position = position;
        this.uid = uid;
        this.admin = admin;
    }

    public Users() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", position='" + position + '\'' +
                ", password='" + password + '\'' +
                ", uid='" + uid + '\'' +
                ", admin=" + admin +
                '}';
    }
}
