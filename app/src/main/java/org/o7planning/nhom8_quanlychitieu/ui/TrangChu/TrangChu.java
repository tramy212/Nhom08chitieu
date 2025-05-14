package org.o7planning.nhom8_quanlychitieu.ui.TrangChu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.models.Transaction;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TrangChu extends Fragment {

    private static final String TAG = "TrangChu";

    // UI Components
    private TextView tvWelcome;
    private TextView tvTotalBalance;
    private TextView tvTotalExpense;
    private TextView tvMonthlyIncome;
    private TextView tvMonthlyExpense;
    private RecyclerView rvRecentTransactions;
    private ImageView ivNotification;
    private TextView tvTime;
    private View statusBarPlaceholder;

    // Adapter
    private HomeTransactionAdapter adapter;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference danhMucRef;

    // Data
    private Map<Integer, DanhMucModel> danhMucMap = new HashMap<>();
    private List<HomeTransactionItem> transactionItems = new ArrayList<>();
    private double totalIncome = 0;
    private double totalExpense = 0;
    private double monthlyIncome = 0;
    private double monthlyExpense = 0;

    // Timer for clock
    private Timer timer;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trang_chu, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Initialize UI components
        initializeUI(root);

        // Setup RecyclerView
        setupRecyclerView();

        // Adjust status bar height
        adjustStatusBarHeight();

        // Load data
        loadDanhMuc();

        // Start clock
        startClock();

        return root;
    }

    private void initializeUI(View root) {
        tvWelcome = root.findViewById(R.id.tv_welcome);
        tvTotalBalance = root.findViewById(R.id.tv_total_balance);
        tvTotalExpense = root.findViewById(R.id.tv_total_expense);
        tvMonthlyIncome = root.findViewById(R.id.tv_monthly_income);
        tvMonthlyExpense = root.findViewById(R.id.tv_monthly_expense);
        rvRecentTransactions = root.findViewById(R.id.rv_recent_transactions);
        ivNotification = root.findViewById(R.id.iv_notification);
        statusBarPlaceholder = root.findViewById(R.id.status_bar_placeholder);

        // Find time TextView in status bar
        View statusBarView = statusBarPlaceholder;
        tvTime = statusBarView.findViewById(R.id.tv_time);

        // Setup notification click
        ivNotification.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thông báo", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HomeTransactionAdapter(getContext(), transactionItems);
        rvRecentTransactions.setAdapter(adapter);
    }

    private void adjustStatusBarHeight() {
        int statusBarHeight = getStatusBarHeight();
        ViewGroup.LayoutParams params = statusBarPlaceholder.getLayoutParams();
        params.height = statusBarHeight;
        statusBarPlaceholder.setLayoutParams(params);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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

                        // Set default category type if not set
                        if (danhMuc.getLoai() == null) {
                            setDefaultCategoryType(danhMuc);
                        }

                        danhMucMap.put(danhMuc.getId(), danhMuc);
                    }
                }

                Log.d(TAG, "Loaded " + danhMucMap.size() + " categories");

                // After loading categories, load transactions
                loadTransactions();

                // Load user data
                loadUserData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading categories: " + error.getMessage());
                Toast.makeText(getContext(), "Error loading categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultCategoryType(DanhMucModel danhMuc) {
        String tenLowerCase = danhMuc.getTen().toLowerCase();

        // Keywords for income categories
        String[] incomeKeywords = {"lương", "thu nhập", "tiền thưởng", "tiền lãi", "cổ tức", "tiền lương", "thưởng", "lãi", "thu"};

        // Check if category name contains income keywords
        for (String keyword : incomeKeywords) {
            if (tenLowerCase.contains(keyword)) {
                danhMuc.setLoai("income");

                // Update category type in Firebase
                danhMucRef.child(danhMuc.getFirebaseKey()).child("loai").setValue("income");
                return;
            }
        }

        // If not income, set as expense by default
        danhMuc.setLoai("expense");
        danhMucRef.child(danhMuc.getFirebaseKey()).child("loai").setValue("expense");
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get user data from Firebase Database
            mDatabase.child("Users").orderByChild("email").equalTo(currentUser.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String name = snapshot.child("name").getValue(String.class);
                                    if (name != null) {
                                        tvWelcome.setText("Chào Mừng " + name + "!");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Error loading user data: " + databaseError.getMessage());
                        }
                    });
        }
    }

    private void loadTransactions() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please login to view transactions", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference transactionsRef = mDatabase.child("Transactions");

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar months are 0-based
        int currentYear = calendar.get(Calendar.YEAR);

        transactionsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionItems.clear();
                totalIncome = 0;
                totalExpense = 0;
                monthlyIncome = 0;
                monthlyExpense = 0;

                // Map to group transactions by date
                Map<String, List<HomeTransactionItem>> transactionsByDate = new HashMap<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        // Get category info
                        DanhMucModel danhMuc = danhMucMap.get(transaction.getDanhMucId());
                        String category = danhMuc != null ? danhMuc.getTen() : "Other";

                        // Determine transaction type (income or expense) based on amount and category
                        String type;
                        if (danhMuc != null && danhMuc.isIncome()) {
                            type = "income";
                            // Ensure positive amount for income
                            if (transaction.getAmount() < 0) {
                                transaction.setAmount(Math.abs(transaction.getAmount()));
                            }
                        } else {
                            type = "expense";
                            // Ensure negative amount for expense
                            if (transaction.getAmount() > 0) {
                                transaction.setAmount(-Math.abs(transaction.getAmount()));
                            }
                        }

                        // Update total income and expense
                        if (transaction.getAmount() >= 0) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpense += Math.abs(transaction.getAmount());
                        }

                        // Check if transaction is from current month
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date transactionDate = sdf.parse(transaction.getDate());
                            Calendar transactionCal = Calendar.getInstance();
                            transactionCal.setTime(transactionDate);
                            int transactionMonth = transactionCal.get(Calendar.MONTH) + 1;
                            int transactionYear = transactionCal.get(Calendar.YEAR);

                            if (transactionMonth == currentMonth && transactionYear == currentYear) {
                                // Update monthly income and expense
                                if (transaction.getAmount() >= 0) {
                                    monthlyIncome += transaction.getAmount();
                                } else {
                                    monthlyExpense += Math.abs(transaction.getAmount());
                                }
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Error parsing date: " + e.getMessage());
                        }

                        // Format amount
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        String formattedAmount = currencyFormat.format(transaction.getAmount());

                        // Create HomeTransactionItem
                        HomeTransactionItem item = new HomeTransactionItem(
                                transaction.getId(),
                                transaction.getTitle(),
                                formattedAmount,
                                transaction.getDate(),
                                type,
                                category,
                                false
                        );

                        // Add to map by date
                        if (!transactionsByDate.containsKey(transaction.getDate())) {
                            transactionsByDate.put(transaction.getDate(), new ArrayList<>());
                        }
                        transactionsByDate.get(transaction.getDate()).add(item);
                    }
                }

                // Sort dates in descending order (newest first)
                List<String> sortedDates = new ArrayList<>(transactionsByDate.keySet());
                Collections.sort(sortedDates, (d1, d2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date1 = sdf.parse(d1);
                        Date date2 = sdf.parse(d2);
                        return date2.compareTo(date1); // Sort in descending order
                    } catch (ParseException e) {
                        return 0;
                    }
                });

                // Add transactions to list with date headers
                for (String date : sortedDates) {
                    // Add date header
                    transactionItems.add(new HomeTransactionItem(date, true));

                    // Add transactions for this date
                    List<HomeTransactionItem> dateTransactions = transactionsByDate.get(date);
                    transactionItems.addAll(dateTransactions);

                    // Limit to 10 transactions for home screen
                    if (transactionItems.size() >= 15) {
                        break;
                    }
                }

                // Update UI
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading transactions: " + error.getMessage());
                Toast.makeText(getContext(), "Error loading transactions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        // Format currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Update total balance
        tvTotalBalance.setText(currencyFormat.format(totalIncome - totalExpense));

        // Update total expense
        tvTotalExpense.setText(currencyFormat.format(-totalExpense));

        // Update monthly income
        tvMonthlyIncome.setText(currencyFormat.format(monthlyIncome));

        // Update monthly expense
        tvMonthlyExpense.setText(currencyFormat.format(monthlyExpense));

        // Update adapter
        adapter.notifyDataSetChanged();
    }

    private void startClock() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    if (tvTime != null) {
                        tvTime.setText(currentTime);
                    }
                });
            }
        }, 0, 60000); // Update every minute
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel timer when fragment is destroyed
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
