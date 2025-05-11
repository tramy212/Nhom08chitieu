package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;

import java.util.ArrayList;
import java.util.List;

public class ThemMoiDanhMucActivity extends AppCompatActivity implements DanhMucMauAdapter.OnItemClickListener {

    private RecyclerView recyclerViewDanhMucMau;
    private DanhMucMauAdapter adapter;
    private List<DanhMucMauModel> danhMucMauList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_moi_danh_muc);

        // Initialize RecyclerView
        recyclerViewDanhMucMau = findViewById(R.id.recyclerViewDanhMucMau);
        recyclerViewDanhMucMau.setLayoutManager(new GridLayoutManager(this, 3));

        // Set up back button
        setupNavigationButtons();

        // Load template categories
        loadDanhMucMau();
    }

    private void setupNavigationButtons() {
        ImageButton backButton = findViewById(R.id.backButton);

        // Set up back button to finish the activity
        backButton.setOnClickListener(v -> finish());
    }

    private void loadDanhMucMau() {
        danhMucMauList = new ArrayList<>();

        // Add template categories
        danhMucMauList.add(new DanhMucMauModel(1, "Spa", "spa", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(2, "Thú Cưng", "pet", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(3, "Du Lịch", "travel", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(4, "Hóa Đơn Điện", "bill", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(5, "Thời Trang", "fashion", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(6, "Chế Độ Uống", "drink", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(7, "Quà", "gift", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(8, "Shopee", "shopping", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(9, "Đầu Tư", "investment", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(10, "Học Tập", "education", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(11, "Skincare", "skincare", "#87CEFA"));
        danhMucMauList.add(new DanhMucMauModel(12, "Xăng", "fuel", "#87CEFA"));

        adapter = new DanhMucMauAdapter(this, danhMucMauList, this);
        recyclerViewDanhMucMau.setAdapter(adapter);
    }

    // Trong phương thức onItemClick của ThemMoiDanhMucActivity
    @Override
    public void onItemClick(DanhMucMauModel danhMucMau) {
        // Create a new DanhMucModel from the template
        DanhMucModel newDanhMuc = new DanhMucModel(
                danhMucMau.getId(),
                danhMucMau.getTen(),
                "Danh mục chi tiêu"
        );

        // Return the selected category to the previous screen
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", newDanhMuc.getId());
        resultIntent.putExtra("ten", newDanhMuc.getTen());
        resultIntent.putExtra("moTa", newDanhMuc.getMoTa());
        resultIntent.putExtra("icon", danhMucMau.getIcon());

        // Sửa từ getColor() thành getMauSac()
        resultIntent.putExtra("mauSac", danhMucMau.getMauSac());

        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Đã thêm danh mục: " + danhMucMau.getTen(), Toast.LENGTH_SHORT).show();
        finish();
    }
}