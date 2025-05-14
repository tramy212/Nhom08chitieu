package org.o7planning.nhom8_quanlychitieu.ui.PhanTich;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.adapter.GoalAdapter;
import org.o7planning.nhom8_quanlychitieu.models.Goal;
import org.o7planning.nhom8_quanlychitieu.models.Transaction;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;
import org.o7planning.nhom8_quanlychitieu.utils.CurrencyFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PhanTichFragment extends Fragment implements GoalAdapter.OnGoalClickListener {

    private static final String TAG = "PhanTichFragment";

    // UI Components
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvMonthSummary;
    private TextView tvAmount;
    private Button btnIncome;
    private Button btnExpense;
    private RecyclerView rvCategoryBars;
    private RecyclerView rvGoals;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference danhMucRef;
    private DatabaseReference goalsRef;

    // Data
    private Map<Integer, DanhMucModel> danhMucMap = new HashMap<>();
    private List<CategoryBarItem> categoryBarItems = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private List<Goal> goals = new ArrayList<>();
    private int selectedMonth;
    private int selectedYear;
    private boolean showingIncome = true; // true for income, false for expense

    // Adapters
    private CategoryBarAdapter categoryBarAdapter;
    private GoalAdapter goalAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_phan_tich, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMuc");
        goalsRef = FirebaseDatabase.getInstance().getReference("Goals");

        // Get arguments
        if (getArguments() != null) {
            selectedMonth = getArguments().getInt("selectedMonth", Calendar.getInstance().get(Calendar.MONTH) + 1);
            selectedYear = getArguments().getInt("selectedYear", Calendar.getInstance().get(Calendar.YEAR));
        } else {
            // Default to current month and year
            Calendar calendar = Calendar.getInstance();
            selectedMonth = calendar.get(Calendar.MONTH) + 1; // Calendar months are 0-based
            selectedYear = calendar.get(Calendar.YEAR);
        }

        // Initialize UI components
        initializeUI(root);

        // Load data
        loadDanhMuc();

        return root;
    }

    private void initializeUI(View root) {
        ivBack = root.findViewById(R.id.iv_back);
        tvTitle = root.findViewById(R.id.tv_title);
        tvMonthSummary = root.findViewById(R.id.tv_month_summary);
        tvAmount = root.findViewById(R.id.tv_amount);
        btnIncome = root.findViewById(R.id.btn_income);
        btnExpense = root.findViewById(R.id.btn_expense);
        rvCategoryBars = root.findViewById(R.id.rv_category_bars);
        rvGoals = root.findViewById(R.id.rv_goals);

        // Setup back button
        ivBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Setup tab buttons
        btnIncome.setOnClickListener(v -> {
            showingIncome = true;
            updateTabButtons();
            updateMonthSummary();
            loadTransactions();
        });

        btnExpense.setOnClickListener(v -> {
            showingIncome = false;
            updateTabButtons();
            updateMonthSummary();
            loadTransactions();
        });

        // Setup RecyclerViews
        setupRecyclerViews();

        // Update month summary
        updateMonthSummary();
    }

    private void setupRecyclerViews() {
        // Setup category bars RecyclerView
        rvCategoryBars.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryBarAdapter = new CategoryBarAdapter(getContext(), categoryBarItems);
        rvCategoryBars.setAdapter(categoryBarAdapter);

        // Setup goals RecyclerView
        rvGoals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        goalAdapter = new GoalAdapter(getContext(), goals, this);
        rvGoals.setAdapter(goalAdapter);
    }

    private void updateTabButtons() {
        if (showingIncome) {
            btnIncome.setBackgroundResource(R.drawable.tab_button_selected);
            btnIncome.setTextColor(getResources().getColor(android.R.color.white));
            btnExpense.setBackgroundResource(R.drawable.tab_button_unselected);
            btnExpense.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            btnIncome.setBackgroundResource(R.drawable.tab_button_unselected);
            btnIncome.setTextColor(getResources().getColor(android.R.color.black));
            btnExpense.setBackgroundResource(R.drawable.tab_button_selected);
            btnExpense.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void updateMonthSummary() {
        String monthName = new org.o7planning.nhom8_quanlychitieu.utils.DateUtils().getMonthName(selectedMonth);
        if (showingIncome) {
            tvMonthSummary.setText("Thu Nhập Tháng " + selectedMonth + "/" + selectedYear + ":");
        } else {
            tvMonthSummary.setText("Chi Tiêu Tháng " + selectedMonth + "/" + selectedYear + ":");
        }
    }

    private void loadDanhMuc() {
        danhMucRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                danhMucMap.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DanhMucModel danhMuc = dataSnapshot.getValue(DanhMucModel.class);
                    if (danhMuc != null) {
                        danhMuc.setFirebaseKey(dataSnapshot.getKey());
                        danhMucMap.put(danhMuc.getId(), danhMuc);
                    }
                }

                // After loading categories, load transactions
                loadTransactions();

                // Load goals
                loadGoals();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading categories: " + error.getMessage());
            }
        });
    }

    private void loadTransactions() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference transactionsRef = mDatabase.child("Transactions");

        transactionsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                categoryBarItems.clear();

                // Map to store total amount by category
                Map<Integer, Double> categoryTotals = new HashMap<>();
                double totalAmount = 0;

                // Process transactions
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        // Add to transactions list
                        transactions.add(transaction);

                        // Check if transaction is from selected month and year
                        if (org.o7planning.nhom8_quanlychitieu.utils.DateUtils.isFromMonth(transaction.getDate(), selectedMonth, selectedYear)) {
                            // Get category
                            DanhMucModel danhMuc = danhMucMap.get(transaction.getDanhMucId());
                            if (danhMuc != null) {
                                boolean isIncome = danhMuc.isIncome();

                                // Only process transactions matching the selected tab (income or expense)
                                if ((showingIncome && isIncome) || (!showingIncome && !isIncome)) {
                                    // Add to category totals
                                    double amount = Math.abs(transaction.getAmount());
                                    int categoryId = transaction.getDanhMucId();

                                    if (categoryTotals.containsKey(categoryId)) {
                                        categoryTotals.put(categoryId, categoryTotals.get(categoryId) + amount);
                                    } else {
                                        categoryTotals.put(categoryId, amount);
                                    }

                                    totalAmount += amount;
                                }
                            }
                        }
                    }
                }

                // Create category bar items
                for (Map.Entry<Integer, Double> entry : categoryTotals.entrySet()) {
                    int categoryId = entry.getKey();
                    double amount = entry.getValue();
                    float percentage = totalAmount > 0 ? (float) (amount / totalAmount * 100) : 0;

                    DanhMucModel danhMuc = danhMucMap.get(categoryId);
                    String categoryName = danhMuc != null ? danhMuc.getTen() : "Other";

                    int colorResId = showingIncome ? R.drawable.rectangle_income : R.drawable.rectangle_expense;

                    CategoryBarItem item = new CategoryBarItem(categoryName, amount, percentage, colorResId);
                    categoryBarItems.add(item);
                }

                // Sort by amount (descending)
                categoryBarItems.sort((item1, item2) -> Double.compare(item2.getAmount(), item1.getAmount()));

                // Update UI
                categoryBarAdapter.notifyDataSetChanged();

                // Update total amount
                CurrencyFormatter formatter = new CurrencyFormatter();
                tvAmount.setText(formatter.formatCurrency(totalAmount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading transactions: " + error.getMessage());
            }
        });
    }

    private void loadGoals() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();
        goalsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goals.clear();

                // Lấy số dư tài khoản hiện tại
                double currentBalance = getCurrentBalance();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Goal goal = dataSnapshot.getValue(Goal.class);
                    if (goal != null) {
                        // Cập nhật số tiền hiện tại bằng số dư tài khoản
                        goal.setCurrentAmount(currentBalance);

                        // Cập nhật lại goal trong Firebase
                        goalsRef.child(goal.getId()).child("currentAmount").setValue(currentBalance);

                        goals.add(goal);
                    }
                }

                goalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading goals: " + error.getMessage());
            }
        });
    }

    // Phương thức lấy số dư tài khoản hiện tại
    private double getCurrentBalance() {
        double totalIncome = 0;
        double totalExpense = 0;

        // Tính tổng thu nhập và chi tiêu từ tất cả giao dịch
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() >= 0) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += Math.abs(transaction.getAmount());
            }
        }

        // Số dư = Tổng thu nhập - Tổng chi tiêu
        return totalIncome - totalExpense;
    }

    @Override
    public void onGoalClick(Goal goal) {
        // Show goal details
        Toast.makeText(getContext(), "Mục tiêu: " + goal.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddGoalClick() {
        showAddGoalDialog();
    }

    @Override
    public void onDeleteGoalClick(Goal goal) {
        // Delete goal
        goalsRef.child(goal.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã xóa mục tiêu", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi xóa mục tiêu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Thêm phương thức này vào class PhanTichFragment để hiển thị dialog full width
    private void showAddGoalDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_goal);

        // Thiết lập dialog full width
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);

        // Get dialog views
        EditText etGoalName = dialog.findViewById(R.id.et_goal_name);
        EditText etGoalAmount = dialog.findViewById(R.id.et_goal_amount);
        EditText etGoalDate = dialog.findViewById(R.id.et_goal_date);
        Spinner spinnerCategory = dialog.findViewById(R.id.spinner_category);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);

        // Setup date picker
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etGoalDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etGoalDate.setText(date);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Setup category spinner
        List<String> categoryNames = new ArrayList<>();
        List<Integer> categoryIds = new ArrayList<>();

        for (DanhMucModel danhMuc : danhMucMap.values()) {
            categoryNames.add(danhMuc.getTen());
            categoryIds.add(danhMuc.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Setup save button
        btnSave.setOnClickListener(v -> {
            String name = etGoalName.getText().toString().trim();
            String amountStr = etGoalAmount.getText().toString().trim();
            String dateStr = etGoalDate.getText().toString().trim();

            if (name.isEmpty() || amountStr.isEmpty() || dateStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double targetAmount = Double.parseDouble(amountStr);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date targetDate = sdf.parse(dateStr);

                int position = spinnerCategory.getSelectedItemPosition();
                int categoryId = categoryIds.get(position);
                String categoryName = categoryNames.get(position);

                // Lấy số dư tài khoản hiện tại
                double currentBalance = getCurrentBalance();

                // Create new goal
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String goalId = UUID.randomUUID().toString();
                    Goal goal = new Goal(
                            goalId,
                            currentUser.getUid(),
                            name,
                            targetAmount,
                            currentBalance, // Sử dụng số dư hiện tại
                            targetDate,
                            categoryId,
                            categoryName
                    );

                    // Save to Firebase
                    goalsRef.child(goalId).setValue(goal)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Mục tiêu đã được thêm", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup close button
        ivClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Thêm phương thức này để cập nhật mục tiêu mỗi khi có giao dịch mới
    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật mục tiêu mỗi khi fragment được hiển thị lại
        loadGoals();
    }
}
