    package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

    public class DanhMucMauModel {
        private int id;
        private String ten;
        private String icon;
        private String mauSac;

        public DanhMucMauModel(int id, String ten, String icon, String mauSac) {
            this.id = id;
            this.ten = ten;
            this.icon = icon;
            this.mauSac = mauSac;
        }

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
    }