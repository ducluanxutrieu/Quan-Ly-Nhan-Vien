package com.ducluanxutrieu.quanlynhanvien.Models;

import java.io.Serializable;

public class RequestItem implements Serializable {
    private String startDate;
    private String endDate;
    private int thanDays;
    private String timeRequest;
    private String name;
    private String text;
    private String offType;
    private boolean accept;
    private String requestKey;
    private String uid;

    public RequestItem() {
    }

    public RequestItem(String startDate, String endDate, int thanDays, String timeRequest, String name, String text, String offType, boolean accept, String uid) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.thanDays = thanDays;
        this.timeRequest = timeRequest;
        this.name = name;
        this.text = text;
        this.offType = offType;
        this.accept = accept;
        this.uid = uid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getThanDays() {
        return thanDays;
    }

    public void setThanDays(int thanDays) {
        this.thanDays = thanDays;
    }

    public String getTimeRequest() {
        return timeRequest;
    }

    public void setTimeRequest(String timeRequest) {
        this.timeRequest = timeRequest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOffType() {
        return offType;
    }

    public void setOffType(String offType) {
        this.offType = offType;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
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
