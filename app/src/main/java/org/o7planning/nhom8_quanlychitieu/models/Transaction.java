package org.o7planning.nhom8_quanlychitieu.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private double amount;
    private int danhMucId;
    private String date;
    private String note;
    private String title;
    private String userId;

    // Constructor mặc định cần thiết cho Firebase
    public Transaction() {
    }

    public Transaction(String id, double amount, int danhMucId, String date, String note, String title, String userId) {
        this.id = id;
        this.amount = amount;
        this.danhMucId = danhMucId;
        this.date = date;
        this.note = note;
        this.title = title;
        this.userId = userId;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(int danhMucId) {
        this.danhMucId = danhMucId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
