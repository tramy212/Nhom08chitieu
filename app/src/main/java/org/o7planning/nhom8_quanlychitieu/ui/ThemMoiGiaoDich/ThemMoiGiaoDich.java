package org.o7planning.nhom8_quanlychitieu.ui.ThemMoiGiaoDich;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.adapter.DanhMucPickerAdapter;
import org.o7planning.nhom8_quanlychitieu.models.Transaction;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.ThemMoiDanhMucActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ThemMoiGiaoDich extends Fragment {

    private static final String TAG = "ThemMoiGiaoDich";
    private static final int REQUEST_ADD_CATEGORY = 1001;

    // UI components
    private EditText etDate, etAmount, etTitle, etNote;
    private TextView tvDanhMuc;
    private Button btnSave;
    private ImageView backBtn;
    private RelativeLayout danhMucContainer;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference danhMucRef;

    // Data
    private List<DanhMucModel> danhMucList;
    private DanhMucModel selectedDanhMuc;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_them_moi_giao_dich, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Initialize date formatter
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();

        // Initialize UI components
        initializeUI(root);

        // Set current date
        etDate.setText(dateFormatter.format(calendar.getTime()));

        // Load danh mục
        loadDanhMuc();

        // Set click listeners
        setClickListeners();

        return root;
    }

    private void initializeUI(View root) {
        etDate = root.findViewById(R.id.etDate);
        etAmount = root.findViewById(R.id.etAmount);
        etTitle = root.findViewById(R.id.etTitle);
        etNote = root.findViewById(R.id.etNote);
        tvDanhMuc = root.findViewById(R.id.tvDanhMuc);
        btnSave = root.findViewById(R.id.btnSave);
        backBtn = root.findViewById(R.id.backBtn);
        danhMucContainer = root.findViewById(R.id.danhMucContainer);
    }

    private void setClickListeners() {
        // Date click listener
        etDate.setOnClickListener(v -> showDatePicker());

        // Danh mục click listener
        danhMucContainer.setOnClickListener(v -> showDanhMucPicker());

        // Save button click listener
        btnSave.setOnClickListener(v -> saveTransaction());

        // Back button click listener
        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etDate.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadDanhMuc() {
        danhMucList = new ArrayList<>();

        danhMucRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                danhMucList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DanhMucModel danhMuc = dataSnapshot.getValue(DanhMucModel.class);
                    if (danhMuc != null) {
                        danhMuc.setFirebaseKey(dataSnapshot.getKey());
                        danhMucList.add(danhMuc);
                    }
                }

                Log.d(TAG, "Loaded " + danhMucList.size() + " danh mục");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading danh mục: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi tải danh mục: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDanhMucPicker() {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_danh_muc_picker, null);
        builder.setView(dialogView);

        // Initialize RecyclerView
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewDanhMuc);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Initialize adapter
        DanhMucPickerAdapter adapter = new DanhMucPickerAdapter(getContext(), danhMucList, danhMuc -> {
            // Khi người dùng chọn một danh mục
            selectedDanhMuc = danhMuc;
        });

        // Nếu đã có danh mục được chọn, hiển thị nó
        if (selectedDanhMuc != null) {
            for (int i = 0; i < danhMucList.size(); i++) {
                if (danhMucList.get(i).getId() == selectedDanhMuc.getId()) {
                    adapter.setSelectedPosition(i);
                    break;
                }
            }
        }

        recyclerView.setAdapter(adapter);

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Add new danh mục button
        Button btnAddNewDanhMuc = dialogView.findViewById(R.id.btnAddNewDanhMuc);
        btnAddNewDanhMuc.setOnClickListener(v -> {
            // Open ThemMoiDanhMucActivity
            Intent intent = new Intent(getActivity(), ThemMoiDanhMucActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);

            // Dismiss dialog
            dialog.dismiss();
        });

        // Cancel button
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Confirm button
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> {
            selectedDanhMuc = adapter.getSelectedDanhMuc();
            if (selectedDanhMuc != null) {
                tvDanhMuc.setText(selectedDanhMuc.getTen());
            }
            dialog.dismiss();
        });
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

            // Cập nhật danh mục đã chọn
            selectedDanhMuc = newDanhMuc;
            tvDanhMuc.setText(selectedDanhMuc.getTen());

            // Reload danh mục
            loadDanhMuc();
        }
    }

    private void saveTransaction() {
        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get data from form
        String date = etDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        // Validate data
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            etAmount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            etTitle.requestFocus();
            return;
        }

        if (selectedDanhMuc == null) {
            Toast.makeText(getContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert amount
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            etAmount.requestFocus();
            return;
        }

        // Create new transaction ID
        String transactionId = mDatabase.child("Transactions").push().getKey();
        if (transactionId == null) {
            Toast.makeText(getContext(), "Không thể tạo ID cho giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new transaction
        Transaction transaction = new Transaction(
                transactionId,
                amount,
                selectedDanhMuc.getId(),
                date,
                note,
                title,
                currentUser.getUid()
        );

        // Save transaction to Firebase
        mDatabase.child("Transactions").child(transactionId).setValue(transaction)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Giao dịch đã được lưu thành công", Toast.LENGTH_SHORT).show();
                        clearForm();
                        // Navigate back
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearForm() {
        etDate.setText(dateFormatter.format(new Date()));
        etAmount.setText("");
        etTitle.setText("");
        etNote.setText("");
        tvDanhMuc.setText("Chọn Danh Mục");
        selectedDanhMuc = null;
    }
}
