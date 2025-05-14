package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

public class DanhMucModel {
    private int id;
    private String ten;
    private String moTa;
    private String icon;
    private String mauSac;
    private String firebaseKey;
    private String loai; // "income" hoặc "expense"

    // Constructor rỗng cần thiết cho Firebase
    public DanhMucModel() {
    }

    public DanhMucModel(int id, String ten, String moTa) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.loai = "expense"; // Mặc định là chi tiêu
    }

    // Thêm constructor mới với 5 tham số
    public DanhMucModel(int id, String ten, String moTa, String icon, String mauSac) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.icon = icon;
        this.mauSac = mauSac;
        this.loai = "expense"; // Mặc định là chi tiêu
    }

    // Thêm constructor mới với 6 tham số bao gồm loại
    public DanhMucModel(int id, String ten, String moTa, String icon, String mauSac, String loai) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.icon = icon;
        this.mauSac = mauSac;
        this.loai = loai;
    }

    // Getters và setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public boolean isIncome() {
        return "income".equals(loai);
    }

    public boolean isExpense() {
        return "expense".equals(loai) || loai == null;
    }
}