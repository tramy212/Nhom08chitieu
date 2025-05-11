package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;

import java.util.ArrayList;
import java.util.List;

public class DanhMuc extends Fragment implements DanhMucAdapter.OnItemClickListener, DanhMucAdapter.OnDeleteClickListener {

    private static final String TAG = "DanhMuc";
    private RecyclerView recyclerViewDanhMuc;
    private DanhMucAdapter danhMucAdapter;
    private List<DanhMucModel> danhMucList;

    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference danhMucRef;

    // Thêm hằng số cho request code
    private static final int REQUEST_ADD_CATEGORY = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_danh_muc, container, false);

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        danhMucRef = database.getReference("DanhMuc");

        // Khởi tạo RecyclerView
        recyclerViewDanhMuc = root.findViewById(R.id.recyclerViewDanhMuc);
        recyclerViewDanhMuc.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Thiết lập các nút điều hướng
        setupNavigationButtons(root);

        // Khởi tạo danh sách rỗng
        danhMucList = new ArrayList<>();
        danhMucAdapter = new DanhMucAdapter(getContext(), danhMucList, this, this);
        recyclerViewDanhMuc.setAdapter(danhMucAdapter);

        // Tải danh sách danh mục từ Firebase
        loadDanhMucFromFirebase();

        return root;
    }

    private void setupNavigationButtons(View view) {
        ImageButton backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Quay lại màn hình trước đó
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadDanhMucFromFirebase() {
        danhMucRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                danhMucList.clear();

                // Đọc danh mục từ Firebase
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DanhMucModel danhMuc = dataSnapshot.getValue(DanhMucModel.class);
                    if (danhMuc != null) {
                        // Lưu key của Firebase vào danhMuc để dễ dàng xóa sau này
                        danhMuc.setFirebaseKey(dataSnapshot.getKey());
                        danhMucList.add(danhMuc);
                    }
                }

                // Thêm nút "Thêm" ở cuối danh sách
                DanhMucModel addButton = new DanhMucModel(0, "Thêm", "Thêm danh mục mới");
                addButton.setIcon("add");
                danhMucList.add(addButton);

                // Cập nhật RecyclerView
                danhMucAdapter.notifyDataSetChanged();

                Log.d(TAG, "Đã tải " + (danhMucList.size() - 1) + " danh mục từ Firebase");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi khi tải danh mục: " + error.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(DanhMucModel danhMuc) {
        if ("Thêm".equals(danhMuc.getTen())) {
            // Mở màn hình ThemMoiDanhMucActivity
            Intent intent = new Intent(getActivity(), ThemMoiDanhMucActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);
        } else {
            // Xử lý khi click vào danh mục thông thường
            Toast.makeText(getContext(), "Đã chọn: " + danhMuc.getTen(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_CATEGORY && resultCode == Activity.RESULT_OK && data != null) {
            // Lấy thông tin danh mục mới từ kết quả trả về
            int id = data.getIntExtra("id", -1);
            String ten = data.getStringExtra("ten");
            String moTa = data.getStringExtra("moTa");
            String icon = data.getStringExtra("icon");
            String mauSac = data.getStringExtra("mauSac");

            // Tạo danh mục mới
            DanhMucModel newDanhMuc = new DanhMucModel(id, ten, moTa);
            newDanhMuc.setIcon(icon);
            if (mauSac != null) {
                newDanhMuc.setMauSac(mauSac);
            }

            // Thêm danh mục mới vào Firebase
            String key = danhMucRef.push().getKey();
            if (key != null) {
                danhMucRef.child(key).setValue(newDanhMuc)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Đã thêm danh mục: " + ten, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Đã thêm danh mục: " + ten);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Lỗi khi thêm danh mục: " + e.getMessage());
                        });
            }
        }
    }

    @Override
    public void onDeleteClick(int position) {
        if (position >= 0 && position < danhMucList.size() - 1) { // Không xóa nút "Thêm"
            DanhMucModel danhMuc = danhMucList.get(position);
            String key = danhMuc.getFirebaseKey();

            if (key != null) {
                // Xóa danh mục từ Firebase
                danhMucRef.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Đã xóa danh mục: " + danhMuc.getTen(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Đã xóa danh mục: " + danhMuc.getTen());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Lỗi khi xóa danh mục: " + e.getMessage());
                        });
            }
        }
    }
}