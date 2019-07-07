package com.ducluanxutrieu.quanlynhanvien.Models;

public class Auth {
    private String email;
    private String name;
    private String uid;

    public Auth() {
    }

    public Auth(String email, String name, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
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

    @Override
    public String toString() {
        return "Auth{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
