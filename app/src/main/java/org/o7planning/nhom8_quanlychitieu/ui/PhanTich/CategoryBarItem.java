package org.o7planning.nhom8_quanlychitieu.ui.PhanTich;

public class CategoryBarItem {
    private String categoryName;
    private double amount;
    private float percentage;
    private int colorResId;

    public CategoryBarItem(String categoryName, double amount, float percentage, int colorResId) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.percentage = percentage;
        this.colorResId = colorResId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }
}
