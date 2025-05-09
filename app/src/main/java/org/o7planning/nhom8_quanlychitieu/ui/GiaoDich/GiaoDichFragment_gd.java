package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.o7planning.nhom8_quanlychitieu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GiaoDichFragment_gd extends Fragment implements TransactionAdapter_gd.OnTransactionActionListener {
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
    private TextView tvTime; // Thêm biến cho TextView hiển thị thời gian
    private Timer timer; // Thêm biến Timer để cập nhật thời gian
    private Handler handler = new Handler(Looper.getMainLooper()); // Handler để cập nhật UI

    // Biến để lưu trạng thái lọc
    private static final int FILTER_NONE = 0;
    private static final int FILTER_INCOME = 1;
    private static final int FILTER_EXPENSE = 2;
    private int currentFilter = FILTER_NONE;

    // Lưu danh sách giao dịch gốc
    private List<TransactionItem_gd> allTransactions = new ArrayList<>();

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

        // Load sample data
        loadSampleData();

        // Cập nhật tiêu đề dựa trên trạng thái lọc
        updateTitle();

        // Bắt đầu cập nhật thời gian
        startClock();

        return root;
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

    private void loadSampleData() {
        // Set summary data
        tvTotalBalance.setText("$7,783.00");
        tvIncome.setText("$4,120.00");
        tvExpense.setText("$1,187.40");

        // Create sample transactions
        allTransactions = new ArrayList<>();

        // Tháng Năm (hiển thị đầu tiên vì là tháng hiện tại)
        allTransactions.add(new TransactionItem_gd("Tháng Năm", true));
        allTransactions.add(new TransactionItem_gd("Lương", "15:30 - 30/5", "$4,200.00", R.drawable.ic_salary_gd, false));
        allTransactions.add(new TransactionItem_gd("Tạp Hóa", "14:45 - 25/5", "-$120.50", R.drawable.ic_groceries_gd, false));
        allTransactions.add(new TransactionItem_gd("Tiền Điện", "10:15 - 15/5", "-$48.30", R.drawable.ic_tax_gd, false));
        allTransactions.add(new TransactionItem_gd("Tiền Nước", "10:10 - 15/5", "-$25.75", R.drawable.ic_tax_gd, false));
        allTransactions.add(new TransactionItem_gd("Phương Tiện", "8:20 - 10/5", "-$15.60", R.drawable.ic_transportation_gd, false));
        allTransactions.add(new TransactionItem_gd("Ăn Uống", "19:45 - 5/5", "-$35.25", R.drawable.ic_food_gd, false));

        // Tháng Tư
        allTransactions.add(new TransactionItem_gd("Tháng Tư", true));
        allTransactions.add(new TransactionItem_gd("Lương", "18:27 - 30/4", "$4,000.00", R.drawable.ic_salary_gd, false));
        allTransactions.add(new TransactionItem_gd("Tạp Hóa", "17:00 - 24/4", "-$100.00", R.drawable.ic_groceries_gd, false));
        allTransactions.add(new TransactionItem_gd("Thuế", "8:30 - 15/4", "-$674.40", R.drawable.ic_tax_gd, false));
        allTransactions.add(new TransactionItem_gd("Phương Tiện", "7:30 - 8/4", "-$4.13", R.drawable.ic_transportation_gd, false));

        // Tháng Ba
        allTransactions.add(new TransactionItem_gd("Tháng Ba", true));
        allTransactions.add(new TransactionItem_gd("Thực Phẩm", "19:30 - 31/3", "-$70.40", R.drawable.ic_food_gd, false));
        allTransactions.add(new TransactionItem_gd("Lương", "15:45 - 28/3", "$3,800.00", R.drawable.ic_salary_gd, false));
        allTransactions.add(new TransactionItem_gd("Mua Sắm", "14:20 - 20/3", "-$250.30", R.drawable.ic_groceries_gd, false));
        allTransactions.add(new TransactionItem_gd("Tiền Điện", "9:15 - 15/3", "-$45.75", R.drawable.ic_tax_gd, false));
        allTransactions.add(new TransactionItem_gd("Tiền Nước", "9:10 - 15/3", "-$22.50", R.drawable.ic_tax_gd, false));

        // Tháng Hai
        allTransactions.add(new TransactionItem_gd("Tháng Hai", true));
        allTransactions.add(new TransactionItem_gd("Lương", "16:30 - 28/2", "$3,800.00", R.drawable.ic_salary_gd, false));
        allTransactions.add(new TransactionItem_gd("Thực Phẩm", "18:45 - 25/2", "-$85.20", R.drawable.ic_food_gd, false));
        allTransactions.add(new TransactionItem_gd("Phương Tiện", "7:30 - 20/2", "-$12.50", R.drawable.ic_transportation_gd, false));
        allTransactions.add(new TransactionItem_gd("Mua Sắm", "13:15 - 15/2", "-$120.75", R.drawable.ic_groceries_gd, false));

        // Hiển thị tất cả giao dịch ban đầu
        adapter.setTransactions(allTransactions);
    }

    @Override
    public void onEditClick(TransactionItem_gd transaction, int position) {
        Toast.makeText(getContext(), "Chỉnh sửa: " + transaction.getName(), Toast.LENGTH_SHORT).show();
        // Thêm code để mở màn hình chỉnh sửa giao dịch
    }

    @Override
    public void onDeleteClick(TransactionItem_gd transaction, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa giao dịch")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Xóa giao dịch khỏi danh sách
                    List<TransactionItem_gd> currentList = new ArrayList<>(adapter.getTransactions());
                    currentList.remove(position);

                    // Cập nhật danh sách hiển thị
                    adapter.setTransactions(currentList);

                    // Cập nhật danh sách gốc
                    for (int i = 0; i < allTransactions.size(); i++) {
                        if (!allTransactions.get(i).isHeader() &&
                                allTransactions.get(i).getName().equals(transaction.getName()) &&
                                allTransactions.get(i).getDate().equals(transaction.getDate())) {
                            allTransactions.remove(i);
                            break;
                        }
                    }

                    Toast.makeText(getContext(), "Đã xóa: " + transaction.getName(), Toast.LENGTH_SHORT).show();
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