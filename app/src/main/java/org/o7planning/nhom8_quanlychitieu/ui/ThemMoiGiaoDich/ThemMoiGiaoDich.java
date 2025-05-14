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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import org.o7planning.nhom8_quanlychitieu.ui.GiaoDich.GiaoDichFragment_gd;

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
    private String originalDate = "";
    private String originalCategory = "";
    private double originalAmount = 0; // Lưu số tiền gốc khi chỉnh sửa
    private Transaction originalTransaction = null; // Lưu giao dịch gốc khi chỉnh sửa

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

                // Nếu đang chỉnh sửa, tải giao dịch gốc từ Firebase
                loadOriginalTransaction(transactionId);

                etTitle.setText(title);

                // Xử lý số tiền - loại bỏ dấu phẩy và ký tự đặc biệt
                amount = cleanAmountString(amount);

                // Kiểm tra nếu số tiền là âm (chi tiêu)
                if (amount.startsWith("-")) {
                    isIncome = false;
                    amount = amount.substring(1); // Loại bỏ dấu trừ
                } else {
                    isIncome = true;
                }

                try {
                    originalAmount = Double.parseDouble(amount);
                    if (!isIncome) {
                        originalAmount = -originalAmount; // Lưu số tiền gốc với dấu
                    }
                } catch (NumberFormatException e) {
                    originalAmount = 0;
                    Log.e(TAG, "Lỗi khi parse số tiền: " + e.getMessage());
                }

                etAmount.setText(amount);

                // Xử lý ngày tháng - chuyển từ dd/MM sang dd/MM/yyyy
                if (date.matches("\\d{1,2}/\\d{1,2}")) {
                    // Nếu chỉ có ngày và tháng, thêm năm hiện tại
                    Calendar cal = Calendar.getInstance();
                    date = date + "/" + cal.get(Calendar.YEAR);
                }

                etDate.setText(date);
                originalDate = date;

                tvDanhMuc.setText(category);
                originalCategory = category;

                tvTitle.setText("Chỉnh Sửa Giao Dịch");

                // Tìm ID của danh mục dựa trên tên
                findCategoryIdByName(category);
            }
        }

        // Set up click listeners
        setupClickListeners();

        // Load danh mục
        loadDanhMuc();

        // Thêm TextWatcher để xử lý số tiền
        setupAmountTextWatcher();

        return root;
    }

    private void loadOriginalTransaction(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            return;
        }

        mDatabase.child("Transactions").child(transactionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalTransaction = snapshot.getValue(Transaction.class);
                if (originalTransaction != null) {
                    Log.d(TAG, "Đã tải giao dịch gốc: " + originalTransaction.getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tải giao dịch gốc: " + error.getMessage());
            }
        });
    }

    private String cleanAmountString(String amount) {
        // Loại bỏ tất cả các ký tự không phải số, dấu chấm và dấu trừ ở đầu
        if (amount == null) return "";

        // Giữ lại dấu trừ ở đầu nếu có
        boolean isNegative = amount.startsWith("-");

        // Loại bỏ tất cả các ký tự không phải số và dấu chấm
        String cleanAmount = amount.replaceAll("[^0-9.]", "");

        // Thêm lại dấu trừ nếu cần
        return isNegative ? "-" + cleanAmount : cleanAmount;
    }

    private void setupAmountTextWatcher() {
        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(current)) {
                    return; // Tránh vòng lặp vô hạn
                }

                String amountText = s.toString();

                // Xử lý dấu + và -
                if (amountText.startsWith("+")) {
                    isIncome = true;
                    // Xóa dấu + để tránh lỗi khi parse số
                    if (amountText.length() > 1) {
                        current = amountText.substring(1);
                        etAmount.setText(current);
                        etAmount.setSelection(current.length());
                    }
                    return;
                } else if (amountText.startsWith("-")) {
                    isIncome = false;
                    // Xóa dấu - để tránh lỗi khi parse số
                    if (amountText.length() > 1) {
                        current = amountText.substring(1);
                        etAmount.setText(current);
                        etAmount.setSelection(current.length());
                    }
                    return;
                }

                // Loại bỏ các ký tự không phải số và dấu chấm
                String cleanText = amountText.replaceAll("[^0-9.]", "");

                // Đảm bảo chỉ có một dấu chấm
                if (cleanText.contains(".")) {
                    int dotIndex = cleanText.indexOf(".");
                    String beforeDot = cleanText.substring(0, dotIndex);
                    String afterDot = cleanText.substring(dotIndex + 1);
                    // Loại bỏ các dấu chấm khác trong phần sau dấu chấm đầu tiên
                    afterDot = afterDot.replace(".", "");
                    cleanText = beforeDot + "." + afterDot;
                }

                // Nếu có sự thay đổi, cập nhật lại text
                if (!cleanText.equals(amountText)) {
                    current = cleanText;
                    etAmount.setText(cleanText);
                    // Đặt con trỏ ở cuối text
                    etAmount.setSelection(cleanText.length());
                } else {
                    current = amountText;
                }
            }
        });
    }

    private void findCategoryIdByName(String categoryName) {
        danhMucRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DanhMucModel danhMuc = dataSnapshot.getValue(DanhMucModel.class);
                    if (danhMuc != null && danhMuc.getTen().equals(categoryName)) {
                        selectedDanhMucId = danhMuc.getId();
                        selectedDanhMucName = danhMuc.getTen();

                        // Cập nhật loại giao dịch dựa trên danh mục
                        isIncome = danhMuc.isIncome();

                        Log.d(TAG, "Đã tìm thấy danh mục: " + danhMuc.getTen() + ", ID: " + danhMuc.getId());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tìm danh mục: " + error.getMessage());
            }
        });
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
            navigateToGiaoDichFragment();
        });
    }

    // Phương thức mới để điều hướng về trang chủ giao dịch
    private void navigateToGiaoDichFragment() {
        if (getActivity() != null) {
            // Tạo instance mới của GiaoDichFragment_gd
            GiaoDichFragment_gd giaoDichFragment = new GiaoDichFragment_gd();

            // Thay thế Fragment hiện tại bằng GiaoDichFragment_gd
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            // Xóa tất cả các Fragment trong back stack
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // Thêm GiaoDichFragment_gd vào container
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, giaoDichFragment)
                    .commit();
        }
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

                        // Nếu đang chỉnh sửa và tên danh mục trùng với danh mục gốc
                        if (isEditing && danhMuc.getTen().equals(originalCategory)) {
                            selectedDanhMucId = danhMuc.getId();
                            selectedDanhMucName = danhMuc.getTen();
                            isIncome = danhMuc.isIncome();
                        }
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

        // Xử lý trường hợp số tiền trống khi chỉnh sửa
        double amount;
        if (amountStr.isEmpty()) {
            if (isEditing && originalTransaction != null) {
                // Nếu đang chỉnh sửa và không nhập số tiền mới, giữ nguyên số tiền cũ
                amount = originalTransaction.getAmount();
            } else {
                etAmount.setError("Vui lòng nhập số tiền");
                return;
            }
        } else {
            // Parse amount
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                etAmount.setError("Số tiền không hợp lệ");
                Log.e(TAG, "Lỗi khi parse số tiền: " + e.getMessage() + ", chuỗi: " + amountStr);
                return;
            }
        }

        // Xử lý trường hợp không chọn danh mục khi chỉnh sửa
        if (selectedDanhMucId == -1) {
            if (isEditing && originalTransaction != null) {
                // Nếu đang chỉnh sửa và không chọn danh mục mới, giữ nguyên danh mục cũ
                selectedDanhMucId = originalTransaction.getDanhMucId();

                // Tìm danh mục trong map để xác định loại giao dịch
                DanhMucModel danhMuc = danhMucMap.get(selectedDanhMucId);
                if (danhMuc != null) {
                    isIncome = danhMuc.isIncome();
                }
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
                return;
            }
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
                        navigateToGiaoDichFragment();
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
                            navigateToGiaoDichFragment();
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