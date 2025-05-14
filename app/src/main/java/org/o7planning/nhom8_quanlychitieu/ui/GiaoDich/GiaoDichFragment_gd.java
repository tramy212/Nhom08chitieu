package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import org.o7planning.nhom8_quanlychitieu.models.Transaction;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;
import org.o7planning.nhom8_quanlychitieu.ui.ThemMoiGiaoDich.ThemMoiGiaoDich;

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

public class GiaoDichFragment_gd extends Fragment implements TransactionAdapter_gd.OnTransactionActionListener {
    private static final String TAG = "GiaoDichFragment_gd";

    private TextView tvTotalBalance;
    private TextView tvIncome;
    private TextView tvExpense;
    private RecyclerView rvTransactions;
    private TransactionAdapter_gd adapter;
    private Toolbar toolbar;
    private ImageView ivNotification;
    private View statusBarPlaceholder;
    private CardView cardIncome;
    private CardView cardExpense;
    private TextView tvIncomeTitle;
    private TextView tvExpenseTitle;
    private ImageView ivIncome;
    private ImageView ivExpense;
    private TextView tvTitle;
    private TextView tvTime;
    private Timer timer;
    private Handler handler = new Handler(Looper.getMainLooper());

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference danhMucRef;

    // Biến để lưu trạng thái lọc
    private static final int FILTER_NONE = 0;
    private static final int FILTER_INCOME = 1;
    private static final int FILTER_EXPENSE = 2;
    private int currentFilter = FILTER_NONE;

    // Lưu danh sách giao dịch gốc
    private List<TransactionItem_gd> allTransactions = new ArrayList<>();

    // Lưu danh sách danh mục
    private Map<Integer, DanhMucModel> danhMucMap = new HashMap<>();

    // Tổng thu nhập và chi tiêu
    private double totalIncome = 0;
    private double totalExpense = 0;

    // Mảng tên tháng tiếng Việt
    private final String[] monthNames = {
            "Tháng Một", "Tháng Hai", "Tháng Ba", "Tháng Tư",
            "Tháng Năm", "Tháng Sáu", "Tháng Bảy", "Tháng Tám",
            "Tháng Chín", "Tháng Mười", "Tháng Mười Một", "Tháng Mười Hai"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_giao_dich_gd, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Initialize views
        tvTotalBalance = root.findViewById(R.id.tv_total_balance);
        tvIncome = root.findViewById(R.id.tv_income);
        tvExpense = root.findViewById(R.id.tv_expense);
        rvTransactions = root.findViewById(R.id.rv_transactions);
        toolbar = root.findViewById(R.id.toolbar);
        statusBarPlaceholder = root.findViewById(R.id.status_bar_placeholder);
        cardIncome = root.findViewById(R.id.card_income);
        cardExpense = root.findViewById(R.id.card_expense);
        tvTitle = root.findViewById(R.id.tv_title);

        // Tìm TextView tiêu đề trong CardView
        tvIncomeTitle = root.findViewById(R.id.tv_income_title);
        tvExpenseTitle = root.findViewById(R.id.tv_expense_title);

        // Tìm ImageView biểu tượng trong layout
        ivIncome = root.findViewById(R.id.iv_income);
        ivExpense = root.findViewById(R.id.iv_expense);

        // Tìm TextView thời gian trong status bar - sửa lại cách tìm
        View statusBarView = statusBarPlaceholder;
        tvTime = statusBarView.findViewById(R.id.tv_time);

        // Điều chỉnh chiều cao của placeholder theo chiều cao thực tế của status bar
        int statusBarHeight = getStatusBarHeight();
        ViewGroup.LayoutParams params = statusBarPlaceholder.getLayoutParams();
        params.height = statusBarHeight;
        statusBarPlaceholder.setLayoutParams(params);

        // Setup toolbar
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        // Setup notification icon click
        ivNotification = toolbar.findViewById(R.id.iv_notification);
        ivNotification.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thông báo", Toast.LENGTH_SHORT).show();
        });

        // Setup RecyclerView
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter_gd(getContext());
        adapter.setOnTransactionActionListener(this);
        rvTransactions.setAdapter(adapter);

        // Setup click listeners for filter cards
        setupFilterCards();

        // Tải danh mục trước
        loadDanhMuc();

        // Cập nhật tiêu đề dựa trên trạng thái lọc
        updateTitle();

        // Bắt đầu cập nhật thời gian
        startClock();

        return root;
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

                        // Nếu loại chưa được đặt, đặt mặc định dựa trên tên danh mục
                        if (danhMuc.getLoai() == null) {
                            setDefaultCategoryType(danhMuc);
                        }

                        danhMucMap.put(danhMuc.getId(), danhMuc);
                    }
                }

                Log.d(TAG, "Đã tải " + danhMucMap.size() + " danh mục");

                // Sau khi tải danh mục xong, tải giao dịch
                loadTransactionsFromFirebase();
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

    private void loadTransactionsFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference transactionsRef = mDatabase.child("Transactions");

        transactionsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTransactions.clear();
                totalIncome = 0;
                totalExpense = 0;

                // Tạo map để nhóm giao dịch theo tháng
                Map<String, List<TransactionItem_gd>> transactionsByMonth = new HashMap<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        // Lấy thông tin danh mục
                        DanhMucModel danhMuc = danhMucMap.get(transaction.getDanhMucId());
                        String category = danhMuc != null ? danhMuc.getTen() : "Khác";

                        // Xác định loại giao dịch (thu nhập hoặc chi tiêu) dựa vào số tiền và danh mục
                        String type;
                        if (danhMuc != null && danhMuc.isIncome()) {
                            type = "income";
                            // Đảm bảo số tiền là dương cho thu nhập
                            if (transaction.getAmount() < 0) {
                                // Cập nhật số tiền trong Firebase
                                mDatabase.child("Transactions").child(transaction.getId()).child("amount")
                                        .setValue(Math.abs(transaction.getAmount()));
                                transaction.setAmount(Math.abs(transaction.getAmount()));
                            }
                        } else {
                            type = "expense";
                            // Đảm bảo số tiền là âm cho chi tiêu
                            if (transaction.getAmount() > 0) {
                                // Cập nhật số tiền trong Firebase
                                mDatabase.child("Transactions").child(transaction.getId()).child("amount")
                                        .setValue(-Math.abs(transaction.getAmount()));
                                transaction.setAmount(-Math.abs(transaction.getAmount()));
                            }
                        }

                        // Cập nhật tổng thu nhập và chi tiêu
                        if (transaction.getAmount() >= 0) {
                            totalIncome += transaction.getAmount();
                        } else {
                            totalExpense += Math.abs(transaction.getAmount());
                        }

                        // Format số tiền
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        String formattedAmount = currencyFormat.format(transaction.getAmount());

                        // Lấy tháng từ ngày giao dịch
                        String month = getMonthFromDate(transaction.getDate());

                        // Tạo đối tượng TransactionItem_gd
                        TransactionItem_gd item = new TransactionItem_gd(
                                transaction.getId(),
                                transaction.getTitle(),
                                formattedAmount,
                                formatDateTime(transaction.getDate()),
                                type,
                                category
                        );

                        // Thêm vào map theo tháng
                        if (!transactionsByMonth.containsKey(month)) {
                            transactionsByMonth.put(month, new ArrayList<>());
                        }
                        transactionsByMonth.get(month).add(item);
                    }
                }

                // Sắp xếp các tháng theo thứ tự giảm dần (tháng mới nhất trước)
                List<String> sortedMonths = new ArrayList<>(transactionsByMonth.keySet());
                Collections.sort(sortedMonths, (m1, m2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                        Date date1 = sdf.parse(m1);
                        Date date2 = sdf.parse(m2);
                        return date2.compareTo(date1); // Sắp xếp giảm dần
                    } catch (ParseException e) {
                        return 0;
                    }
                });

                // Thêm các giao dịch vào danh sách allTransactions theo thứ tự tháng
                for (String month : sortedMonths) {
                    // Thêm header tháng
                    String monthName = getVietnameseMonthName(month);
                    allTransactions.add(new TransactionItem_gd(monthName, true));

                    // Sắp xếp giao dịch trong tháng theo ngày giảm dần
                    List<TransactionItem_gd> monthTransactions = transactionsByMonth.get(month);
                    Collections.sort(monthTransactions, (t1, t2) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                            Date date1 = sdf.parse(t1.getDate());
                            Date date2 = sdf.parse(t2.getDate());
                            return date2.compareTo(date1); // Sắp xếp giảm dần
                        } catch (ParseException e) {
                            return 0;
                        }
                    });

                    // Thêm các giao dịch của tháng
                    allTransactions.addAll(monthTransactions);
                }

                // Cập nhật UI
                updateSummaryData();

                // Hiển thị tất cả giao dịch ban đầu
                adapter.setTransactions(allTransactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tải giao dịch: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi tải giao dịch: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getMonthFromDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Lỗi khi phân tích ngày: " + e.getMessage());
            return "01/2023"; // Giá trị mặc định nếu có lỗi
        }
    }

    private String formatDateTime(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);

            // Format lại thành "dd/MM" - chỉ hiển thị ngày và tháng, không hiển thị giờ
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Lỗi khi định dạng ngày: " + e.getMessage());
            return dateStr; // Trả về chuỗi gốc nếu có lỗi
        }
    }

    private String getVietnameseMonthName(String monthYear) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(monthYear);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);
            return monthNames[month];
        } catch (ParseException e) {
            Log.e(TAG, "Lỗi khi phân tích tháng: " + e.getMessage());
            return "Tháng Không Xác Định";
        }
    }

    private void updateSummaryData() {
        // Format số tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Cập nhật tổng thu nhập
        tvIncome.setText(currencyFormat.format(totalIncome));

        // Cập nhật tổng chi tiêu
        tvExpense.setText(currencyFormat.format(totalExpense));

        // Cập nhật tổng số dư
        double balance = totalIncome - totalExpense;
        tvTotalBalance.setText(currencyFormat.format(balance));
    }

    // Phương thức để bắt đầu cập nhật thời gian
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
        }, 0, 60000); // Cập nhật mỗi phút
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy timer khi fragment bị hủy
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateTitle() {
        // Cập nhật tiêu đề dựa trên trạng thái lọc
        if (currentFilter == FILTER_INCOME) {
            tvTitle.setText("Thu Nhập");
        } else if (currentFilter == FILTER_EXPENSE) {
            tvTitle.setText("Chi Tiêu");
        } else {
            tvTitle.setText("Giao Dịch");
        }
    }

    private void setupFilterCards() {
        // Thiết lập sự kiện click cho thẻ Thu Nhập
        cardIncome.setOnClickListener(v -> {
            if (currentFilter == FILTER_INCOME) {
                // Nếu đang lọc thu nhập, bỏ lọc
                currentFilter = FILTER_NONE;
                updateCardColors();
                filterTransactions();
                updateTitle();
            } else {
                // Nếu chưa lọc hoặc đang lọc chi tiêu, chuyển sang lọc thu nhập
                currentFilter = FILTER_INCOME;
                updateCardColors();
                filterTransactions();
                updateTitle();
            }
        });

        // Thiết lập sự kiện click cho thẻ Chi Tiêu
        cardExpense.setOnClickListener(v -> {
            if (currentFilter == FILTER_EXPENSE) {
                // Nếu đang lọc chi tiêu, bỏ lọc
                currentFilter = FILTER_NONE;
                updateCardColors();
                filterTransactions();
                updateTitle();
            } else {
                // Nếu chưa lọc hoặc đang lọc thu nhập, chuyển sang lọc chi tiêu
                currentFilter = FILTER_EXPENSE;
                updateCardColors();
                filterTransactions();
                updateTitle();
            }
        });
    }

    private void updateCardColors() {
        // Lấy màu từ resources
        int colorBlue = ContextCompat.getColor(getContext(), R.color.gd_blue);
        int colorWhite = Color.WHITE;
        int colorLetters = ContextCompat.getColor(getContext(), R.color.gd_letters_and_icons);

        // Cập nhật màu nền cho thẻ Thu Nhập
        cardIncome.setCardBackgroundColor(currentFilter == FILTER_INCOME ? colorBlue : colorWhite);

        // Cập nhật màu chữ cho thẻ Thu Nhập
        if (tvIncomeTitle != null) {
            tvIncomeTitle.setTextColor(currentFilter == FILTER_INCOME ? colorWhite : colorLetters);
        }

        // Cập nhật màu số tiền cho thẻ Thu Nhập
        tvIncome.setTextColor(currentFilter == FILTER_INCOME ? colorWhite : colorLetters);

        // Cập nhật biểu tượng cho thẻ Thu Nhập
        if (ivIncome != null) {
            ivIncome.setImageResource(currentFilter == FILTER_INCOME ? R.drawable.thunhap2 : R.drawable.thu_nhap);
        }

        // Cập nhật màu nền cho thẻ Chi Tiêu
        cardExpense.setCardBackgroundColor(currentFilter == FILTER_EXPENSE ? colorBlue : colorWhite);

        // Cập nhật màu chữ cho thẻ Chi Tiêu
        if (tvExpenseTitle != null) {
            tvExpenseTitle.setTextColor(currentFilter == FILTER_EXPENSE ? colorWhite : colorLetters);
        }

        // Cập nhật màu số tiền cho thẻ Chi Tiêu
        tvExpense.setTextColor(currentFilter == FILTER_EXPENSE ? colorWhite : colorBlue);

        // Cập nhật biểu tượng cho thẻ Chi Tiêu
        if (ivExpense != null) {
            ivExpense.setImageResource(currentFilter == FILTER_EXPENSE ? R.drawable.chiphi2 : R.drawable.chi_tieu);
        }
    }

    private void filterTransactions() {
        if (currentFilter == FILTER_NONE) {
            // Hiển thị tất cả giao dịch
            processAndDisplayTransactions(allTransactions);
            return;
        }

        // Lọc giao dịch theo loại
        List<TransactionItem_gd> filteredList = new ArrayList<>();

        // Duyệt qua danh sách gốc
        for (int i = 0; i < allTransactions.size(); i++) {
            TransactionItem_gd item = allTransactions.get(i);

            if (item.isHeader()) {
                // Luôn thêm header tháng vào danh sách
                filteredList.add(item);
            } else if ((currentFilter == FILTER_INCOME && item.getType().equals("income")) ||
                    (currentFilter == FILTER_EXPENSE && item.getType().equals("expense"))) {
                // Thêm giao dịch phù hợp với bộ lọc
                filteredList.add(item);
            }
        }

        // Xử lý và hiển thị danh sách đã lọc
        processAndDisplayTransactions(filteredList);
    }

    private void processAndDisplayTransactions(List<TransactionItem_gd> transactions) {
        // Xử lý danh sách để loại bỏ các header không có giao dịch
        List<TransactionItem_gd> processedList = new ArrayList<>();
        String currentMonth = null;
        List<TransactionItem_gd> monthTransactions = new ArrayList<>();

        for (TransactionItem_gd item : transactions) {
            if (item.isHeader()) {
                // Nếu đã có tháng trước đó và có giao dịch, thêm vào danh sách kết quả
                if (currentMonth != null && !monthTransactions.isEmpty()) {
                    processedList.add(new TransactionItem_gd(currentMonth, true));
                    processedList.addAll(monthTransactions);
                }

                // Cập nhật tháng hiện tại và làm mới danh sách giao dịch của tháng
                currentMonth = item.getName();
                monthTransactions.clear();
            } else {
                // Thêm giao dịch vào danh sách tháng hiện tại
                monthTransactions.add(item);
            }
        }

        // Xử lý tháng cuối cùng
        if (currentMonth != null && !monthTransactions.isEmpty()) {
            processedList.add(new TransactionItem_gd(currentMonth, true));
            processedList.addAll(monthTransactions);
        }

        // Cập nhật adapter với danh sách đã xử lý
        adapter.setTransactions(processedList);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onEditClick(TransactionItem_gd transaction, int position) {
        try {
            // Tạo một instance mới của Fragment ThemMoiGiaoDich
            ThemMoiGiaoDich themMoiGiaoDichFragment = new ThemMoiGiaoDich();

            // Tạo Bundle để truyền dữ liệu
            Bundle args = new Bundle();
            args.putBoolean("IS_EDITING", true);
            args.putString("TRANSACTION_ID", transaction.getId());
            args.putString("TRANSACTION_TITLE", transaction.getName());

            // Xử lý số tiền - loại bỏ ký tự đặc biệt
            String amount = transaction.getAmount().replace(".", "")
                    .replace(",", "").replace("₫", "").replace(" ", "").trim();
            args.putString("TRANSACTION_AMOUNT", amount);

            // Xử lý ngày tháng - chuyển từ dd/MM sang dd/MM/yyyy
            String date = transaction.getDate();
            if (date.matches("\\d{1,2}/\\d{1,2}")) {
                // Nếu chỉ có ngày và tháng, thêm năm hiện tại
                Calendar cal = Calendar.getInstance();
                date = date + "/" + cal.get(Calendar.YEAR);
            }
            args.putString("TRANSACTION_DATE", date);

            args.putString("TRANSACTION_CATEGORY", transaction.getCategory());

            // Đặt arguments cho Fragment
            themMoiGiaoDichFragment.setArguments(args);

            // Thay thế Fragment hiện tại bằng Fragment ThemMoiGiaoDich
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, themMoiGiaoDichFragment)
                        .addToBackStack(null)  // Để có thể quay lại Fragment trước đó
                        .commit();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi mở màn hình chỉnh sửa: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi khi mở màn hình chỉnh sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(TransactionItem_gd transaction, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa giao dịch")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Xóa giao dịch khỏi Firebase
                    mDatabase.child("Transactions").child(transaction.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Đã xóa: " + transaction.getName(), Toast.LENGTH_SHORT).show();
                                // Không cần cập nhật UI vì ValueEventListener sẽ tự động cập nhật
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onCalendarClick(String monthYear) {
        showDatePickerDialog(monthYear);
    }

    private void showDatePickerDialog(String monthYear) {
        // Lấy ngày hiện tại
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Nếu có tháng được chọn, đặt calendar về tháng đó
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(monthYear)) {
                month = i;
                break;
            }
        }

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Khi người dùng chọn ngày
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);

                        // Format ngày đã chọn
                        SimpleDateFormat sdf = new SimpleDateFormat("d/M", Locale.getDefault());
                        String dateString = sdf.format(selectedDate.getTime());

                        // Tìm giao dịch có ngày tương ứng
                        scrollToTransaction(dateString);
                    }
                },
                year,
                month,
                day);

        // Bỏ tiêu đề
        datePickerDialog.setTitle(null);

        datePickerDialog.show();
    }

    private void scrollToTransaction(String dateString) {
        List<TransactionItem_gd> transactions = adapter.getTransactions();
        boolean found = false;

        for (int i = 0; i < transactions.size(); i++) {
            TransactionItem_gd item = transactions.get(i);
            if (!item.isHeader() && item.getDate().contains(dateString)) {
                // Nếu tìm thấy giao dịch có ngày tương ứng, cuộn đến vị trí đó
                rvTransactions.smoothScrollToPosition(i);
                found = true;
                break;
            }
        }

        if (!found) {
            // Nếu không tìm thấy giao dịch nào, hiển thị thông báo
            Toast.makeText(getContext(), "Không có giao dịch nào vào ngày " + dateString, Toast.LENGTH_SHORT).show();
        }
    }
}