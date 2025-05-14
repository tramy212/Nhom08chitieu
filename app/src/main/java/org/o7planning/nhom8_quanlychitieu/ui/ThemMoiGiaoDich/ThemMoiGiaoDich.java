package org.o7planning.nhom8_quanlychitieu.ui.ThemMoiGiaoDich;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.models.Transaction;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ThemMoiGiaoDich extends Fragment {

    private static final String TAG = "ThemMoiGiaoDich";

    // UI components
    private EditText etDate, etAmount, etTitle, etNote;
    private TextView tvDanhMuc, tvTitle;
    private Button btnSave;
    private RelativeLayout danhMucContainer;
    private ImageView backBtn;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference danhMucRef;

    // Variables
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private int selectedDanhMucId = -1;
    private String selectedDanhMucName = "";
    private boolean isEditing = false;
    private String transactionId = "";
    private Map<Integer, DanhMucModel> danhMucMap = new HashMap<>();
    private boolean isIncome = false; // Mặc định là chi tiêu

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_them_moi_giao_dich, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Initialize UI components
        etDate = root.findViewById(R.id.etDate);
        etAmount = root.findViewById(R.id.etAmount);
        etTitle = root.findViewById(R.id.etTitle);
        etNote = root.findViewById(R.id.etNote);
        tvDanhMuc = root.findViewById(R.id.tvDanhMuc);
        tvTitle = root.findViewById(R.id.tvTitle);
        btnSave = root.findViewById(R.id.btnSave);
        danhMucContainer = root.findViewById(R.id.danhMucContainer);
        backBtn = root.findViewById(R.id.backBtn);

        // Initialize calendar and date format
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));

        // Check if we're editing an existing transaction
        Bundle args = getArguments();
        if (args != null) {
            isEditing = args.getBoolean("IS_EDITING", false);
            if (isEditing) {
                transactionId = args.getString("TRANSACTION_ID", "");
                String title = args.getString("TRANSACTION_TITLE", "");
                String amount = args.getString("TRANSACTION_AMOUNT", "");
                String date = args.getString("TRANSACTION_DATE", "");
                String category = args.getString("TRANSACTION_CATEGORY", "");

                etTitle.setText(title);
                etAmount.setText(amount);
                etDate.setText(date);
                tvDanhMuc.setText(category);
                tvTitle.setText("Chỉnh Sửa Giao Dịch");
            }
        }

        // Set up click listeners
        setupClickListeners();

        // Load danh mục
        loadDanhMuc();

        // Thêm TextWatcher cho etAmount để xử lý dấu + và -
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String amountText = s.toString();
                if (amountText.startsWith("+")) {
                    isIncome = true;
                    // Xóa dấu + để tránh lỗi khi parse số
                    if (amountText.length() > 1) {
                        etAmount.removeTextChangedListener(this);
                        etAmount.setText(amountText.substring(1));
                        etAmount.setSelection(etAmount.getText().length());
                        etAmount.addTextChangedListener(this);
                    }
                } else if (amountText.startsWith("-")) {
                    isIncome = false;
                    // Xóa dấu - để tránh lỗi khi parse số
                    if (amountText.length() > 1) {
                        etAmount.removeTextChangedListener(this);
                        etAmount.setText(amountText.substring(1));
                        etAmount.setSelection(etAmount.getText().length());
                        etAmount.addTextChangedListener(this);
                    }
                }
            }
        });

        return root;
    }

    private void setupClickListeners() {
        // Date picker
        etDate.setOnClickListener(v -> showDatePickerDialog());

        // Danh mục picker
        danhMucContainer.setOnClickListener(v -> showDanhMucDialog());

        // Save button
        btnSave.setOnClickListener(v -> saveTransaction());

        // Back button
        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadDanhMuc() {
        danhMucRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                danhMucMap.clear();
                List<String> danhMucNames = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DanhMucModel danhMuc = dataSnapshot.getValue(DanhMucModel.class);
                    if (danhMuc != null) {
                        danhMuc.setFirebaseKey(dataSnapshot.getKey());

                        // Nếu loại chưa được đặt, đặt mặc định dựa trên tên danh mục
                        if (danhMuc.getLoai() == null) {
                            setDefaultCategoryType(danhMuc);
                        }

                        danhMucMap.put(danhMuc.getId(), danhMuc);
                        danhMucNames.add(danhMuc.getTen());
                    }
                }

                Log.d(TAG, "Đã tải " + danhMucMap.size() + " danh mục");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tải danh mục: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi tải danh mục: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultCategoryType(DanhMucModel danhMuc) {
        String tenLowerCase = danhMuc.getTen().toLowerCase();

        // Danh sách các từ khóa cho danh mục thu nhập
        String[] incomeKeywords = {"lương", "thu nhập", "tiền thưởng", "tiền lãi", "cổ tức", "tiền lương", "thưởng", "lãi", "thu"};

        // Kiểm tra xem tên danh mục có chứa từ khóa thu nhập không
        for (String keyword : incomeKeywords) {
            if (tenLowerCase.contains(keyword)) {
                danhMuc.setLoai("income");

                // Cập nhật loại danh mục trong Firebase
                danhMucRef.child(danhMuc.getFirebaseKey()).child("loai").setValue("income");
                return;
            }
        }

        // Nếu không phải thu nhập, đặt mặc định là chi tiêu
        danhMuc.setLoai("expense");
        danhMucRef.child(danhMuc.getFirebaseKey()).child("loai").setValue("expense");
    }

    private void showDanhMucDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn Danh Mục");

        // Convert map to array for dialog
        final String[] danhMucArray = new String[danhMucMap.size()];
        final Integer[] danhMucIds = new Integer[danhMucMap.size()];
        int i = 0;
        for (Map.Entry<Integer, DanhMucModel> entry : danhMucMap.entrySet()) {
            danhMucArray[i] = entry.getValue().getTen();
            danhMucIds[i] = entry.getKey();
            i++;
        }

        builder.setItems(danhMucArray, (dialog, which) -> {
            selectedDanhMucId = danhMucIds[which];
            selectedDanhMucName = danhMucArray[which];
            tvDanhMuc.setText(selectedDanhMucName);

            // Cập nhật loại giao dịch dựa trên danh mục đã chọn
            DanhMucModel selectedDanhMuc = danhMucMap.get(selectedDanhMucId);
            if (selectedDanhMuc != null) {
                isIncome = selectedDanhMuc.isIncome();
            }
        });

        builder.show();
    }

    private void saveTransaction() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        // Validate input
        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            return;
        }

        if (amountStr.isEmpty()) {
            etAmount.setError("Vui lòng nhập số tiền");
            return;
        }

        if (selectedDanhMucId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse amount
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Số tiền không hợp lệ");
            return;
        }

        // Xác định loại giao dịch dựa trên danh mục đã chọn
        DanhMucModel selectedDanhMuc = danhMucMap.get(selectedDanhMucId);
        if (selectedDanhMuc != null) {
            isIncome = selectedDanhMuc.isIncome();
        }

        // Điều chỉnh số tiền dựa trên loại giao dịch
        if (!isIncome && amount > 0) {
            amount = -amount; // Chuyển thành số âm cho chi tiêu
        } else if (isIncome && amount < 0) {
            amount = Math.abs(amount); // Đảm bảo số dương cho thu nhập
        }

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create transaction object
        Transaction transaction = new Transaction();
        transaction.setTitle(title);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setNote(note);
        transaction.setDanhMucId(selectedDanhMucId);
        transaction.setUserId(currentUser.getUid());

        // Save to Firebase
        if (isEditing && !transactionId.isEmpty()) {
            // Update existing transaction
            transaction.setId(transactionId);
            mDatabase.child("Transactions").child(transactionId).setValue(transaction)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Giao dịch đã được cập nhật", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Add new transaction
            String key = mDatabase.child("Transactions").push().getKey();
            if (key != null) {
                transaction.setId(key);
                mDatabase.child("Transactions").child(key).setValue(transaction)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Giao dịch đã được thêm", Toast.LENGTH_SHORT).show();
                            clearForm();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    private void clearForm() {
        etTitle.setText("");
        etAmount.setText("");
        etNote.setText("");
        tvDanhMuc.setText("Chọn Danh Mục");
        selectedDanhMucId = -1;
        selectedDanhMucName = "";
        calendar = Calendar.getInstance();
        etDate.setText(dateFormat.format(calendar.getTime()));
    }
}