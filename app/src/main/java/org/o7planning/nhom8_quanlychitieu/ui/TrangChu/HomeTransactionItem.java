package org.o7planning.nhom8_quanlychitieu.ui.TrangChu;

import org.o7planning.nhom8_quanlychitieu.R;

public class HomeTransactionItem {
    private String id;
    private String name;
    private String amount;
    private String date;
    private String type; // "income" or "expense"
    private String category;
    private boolean isHeader;
    private int iconResource;

    // Constructor for date header
    public HomeTransactionItem(String date, boolean isHeader) {
        this.id = "";
        this.name = "";
        this.amount = "";
        this.date = date;
        this.type = "";
        this.category = "";
        this.isHeader = isHeader;
        this.iconResource = 0;
    }

    // Constructor for transaction
    public HomeTransactionItem(String id, String name, String amount, String date, String type, String category, boolean isHeader) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        this.isHeader = isHeader;
        this.iconResource = getIconResourceFromCategory(category);
    }

    private int getIconResourceFromCategory(String category) {
        category = category.toLowerCase();
        if (category.contains("lương") || category.contains("thu nhập"))
            return R.drawable.ic_salary_gd;
        if (category.contains("thực phẩm"))
            return R.drawable.ic_restaurant_menu_black_24dp;
        if (category.contains("mua sắm"))
            return R.drawable.ic_shopping;
        if (category.contains("hóa đơn"))
            return R.drawable.ic_bill;
        if (category.contains("di chuyển") || category.contains("xăng"))
            return R.drawable.ic_directions_car_black_24dp;
        if (category.contains("giải trí"))
            return R.drawable.ic_local_movies_black_24dp;
        if (category.contains("học tập"))
            return R.drawable.ic_education;
        if (category.contains("sức khỏe"))
            return R.drawable.ic_local_hospital_black_24dp;
        if (category.contains("thời trang"))
            return R.drawable.ic_fashion;
        if (category.contains("làm đẹp") || category.contains("spa"))
            return R.drawable.ic_spa;
        if (category.contains("du lịch"))
            return R.drawable.ic_travel;
        if (category.contains("quà"))
            return R.drawable.ic_gift;
        return R.drawable.ic_dashboard_black_24dp;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public int getIconResource() {
        return iconResource;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
}
