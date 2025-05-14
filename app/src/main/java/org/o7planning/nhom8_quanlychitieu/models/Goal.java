package org.o7planning.nhom8_quanlychitieu.models;

import java.util.Date;

public class Goal {
    private String id;
    private String userId;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private Date targetDate;
    private int categoryId;
    private String categoryName;
    private boolean isCompleted;

    public Goal() {
        // Default constructor required for Firebase
    }

    public Goal(String id, String userId, String name, double targetAmount, double currentAmount,
                Date targetDate, int categoryId, String categoryName) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.isCompleted = currentAmount >= targetAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
        // Tự động cập nhật trạng thái hoàn thành khi số tiền hiện tại đạt hoặc vượt mục tiêu
        this.isCompleted = currentAmount >= targetAmount;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Phương thức tính phần trăm hoàn thành
    public float getProgressPercentage() {
        if (targetAmount <= 0) return 0;
        float percentage = (float) ((currentAmount / targetAmount) * 100);
        return Math.min(percentage, 100); // Đảm bảo không vượt quá 100%
    }
}
