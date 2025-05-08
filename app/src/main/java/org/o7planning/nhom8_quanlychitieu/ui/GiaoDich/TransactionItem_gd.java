package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import org.o7planning.nhom8_quanlychitieu.R;

public class TransactionItem_gd {
    private String name;
    private String date;
    private String amount;
    private int iconResource;
    private boolean isHeader;
    private String type; // "income" hoặc "expense"
    private String category;

    // Constructor cho header tháng
    public TransactionItem_gd(String name, boolean isHeader) {
        this.name = name;
        this.isHeader = isHeader;
        this.date = "";
        this.amount = "";
        this.iconResource = 0;
        this.type = "";
        this.category = "";
    }

    // Constructor cho giao dịch
    public TransactionItem_gd(String name, String date, String amount, int iconResource, boolean isHeader) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.iconResource = iconResource;
        this.isHeader = isHeader;

        // Xác định loại giao dịch dựa vào số tiền
        if (amount.startsWith("-")) {
            this.type = "expense";
        } else {
            this.type = "income";
        }

        // Xác định danh mục dựa vào tên
        this.category = getCategoryFromName(name);
    }

    // Constructor đầy đủ
    public TransactionItem_gd(String id, String name, String amount, String date, String type, String category) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.isHeader = false;
        this.type = type;
        this.category = category;
        this.iconResource = getIconResourceFromCategory(category);
    }

    private String getCategoryFromName(String name) {
        name = name.toLowerCase();
        if (name.contains("lương")) return "lương";
        if (name.contains("thực phẩm")) return "thực phẩm";
        if (name.contains("tạp hóa")) return "mua sắm";
        if (name.contains("thuế")) return "thuế";
        if (name.contains("phương tiện") || name.contains("đi lại")) return "đi lại";
        return "khác";
    }

    private int getIconResourceFromCategory(String category) {
        category = category.toLowerCase();
        if (category.contains("lương")) return R.drawable.ic_salary_gd;
        if (category.contains("thực phẩm")) return R.drawable.ic_food_gd;
        if (category.contains("mua sắm")) return R.drawable.ic_groceries_gd;
        if (category.contains("thuế")) return R.drawable.ic_tax_gd;
        if (category.contains("đi lại")) return R.drawable.ic_transportation_gd;
        return type.equals("income") ? R.drawable.ic_income_gd : R.drawable.ic_expense_gd;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public int getIconResource() {
        return iconResource;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }
}