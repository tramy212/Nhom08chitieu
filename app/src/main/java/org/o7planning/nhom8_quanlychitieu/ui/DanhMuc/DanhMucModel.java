package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

public class DanhMucModel {
    private int id;
    private String ten;
    private String moTa;
    private String icon;
    private String mauSac; // Thêm trường này nếu chưa có
    private String firebaseKey;

    // Constructor rỗng cần thiết cho Firebase
    public DanhMucModel() {
    }

    public DanhMucModel(int id, String ten, String moTa) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
    }

    // Thêm constructor mới với 5 tham số
    public DanhMucModel(int id, String ten, String moTa, String icon, String mauSac) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.icon = icon;
        this.mauSac = mauSac;
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
}