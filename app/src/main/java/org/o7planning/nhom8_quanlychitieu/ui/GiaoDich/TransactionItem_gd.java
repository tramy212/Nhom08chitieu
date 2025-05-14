package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import org.o7planning.nhom8_quanlychitieu.R;

public class TransactionItem_gd {
    private String id; // ID của giao dịch
    private String name;
    private String date;
    private String amount;
    private int iconResource;
    private boolean isHeader;
    private String type; // "income" hoặc "expense"
    private String category;

    // Constructor cho header tháng
    public TransactionItem_gd(String name, boolean isHeader) {
        this.id = "";
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
        this.id = "";
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
        this.id = id;
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
        if (name.contains("lương")) return "Lương";
        if (name.contains("thực phẩm")) return "Thực phẩm";
        if (name.contains("tạp hóa")) return "Mua sắm";
        if (name.contains("thuế")) return "Thuế";
        if (name.contains("điện")) return "Hóa đơn";
        if (name.contains("nước")) return "Hóa đơn";
        if (name.contains("internet")) return "Hóa đơn";
        if (name.contains("điện thoại")) return "Hóa đơn";
        if (name.contains("xăng")) return "Xăng";
        if (name.contains("taxi")) return "Di chuyển";
        if (name.contains("xe bus")) return "Di chuyển";
        if (name.contains("xe ôm")) return "Di chuyển";
        if (name.contains("grab")) return "Di chuyển";
        if (name.contains("phim")) return "Giải trí";
        if (name.contains("game")) return "Giải trí";
        if (name.contains("nhạc")) return "Giải trí";
        if (name.contains("sách")) return "Học tập";
        if (name.contains("khóa học")) return "Học tập";
        if (name.contains("học phí")) return "Học tập";
        if (name.contains("bệnh viện")) return "Sức khỏe";
        if (name.contains("thuốc")) return "Sức khỏe";
        if (name.contains("bác sĩ")) return "Sức khỏe";
        if (name.contains("khám")) return "Sức khỏe";
        if (name.contains("quần áo")) return "Thời trang";
        if (name.contains("giày")) return "Thời trang";
        if (name.contains("túi")) return "Thời trang";
        if (name.contains("mỹ phẩm")) return "Làm đẹp";
        if (name.contains("spa")) return "Làm đẹp";
        if (name.contains("tóc")) return "Làm đẹp";
        if (name.contains("du lịch")) return "Du lịch";
        if (name.contains("khách sạn")) return "Du lịch";
        if (name.contains("vé máy bay")) return "Du lịch";
        if (name.contains("quà")) return "Quà tặng";
        if (name.contains("tiền thưởng")) return "Thu nhập";
        if (name.contains("lãi")) return "Thu nhập";
        if (name.contains("cổ tức")) return "Thu nhập";
        if (name.contains("thưởng")) return "Thu nhập";
        return "Khác";
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

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}