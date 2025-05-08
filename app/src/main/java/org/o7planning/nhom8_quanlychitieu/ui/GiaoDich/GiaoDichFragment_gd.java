package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GiaoDichFragment_gd extends Fragment implements TransactionAdapter_gd.OnTransactionActionListener {
    private TextView tvTotalBalance;
    private TextView tvIncome;
    private TextView tvExpense;
    private RecyclerView rvTransactions;
    private TransactionAdapter_gd adapter;
    private Toolbar toolbar;
    private ImageView ivBack;
    private ImageView ivNotification;
    private View statusBarPlaceholder;

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

        // Điều chỉnh chiều cao của placeholder theo chiều cao thực tế của status bar
        int statusBarHeight = getStatusBarHeight();
        ViewGroup.LayoutParams params = statusBarPlaceholder.getLayoutParams();
        params.height = statusBarHeight;
        statusBarPlaceholder.setLayoutParams(params);

        // Setup toolbar
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup back button
        ivBack = toolbar.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

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

        // Load sample data
        loadSampleData();

        return root;
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
        List<TransactionItem_gd> transactions = new ArrayList<>();

        // Tháng Năm (hiển thị đầu tiên vì là tháng hiện tại)
        transactions.add(new TransactionItem_gd("Tháng Năm", true));
        transactions.add(new TransactionItem_gd("Lương", "15:30 - 30/5", "$4,200.00", R.drawable.ic_salary_gd, false));
        transactions.add(new TransactionItem_gd("Tạp Hóa", "14:45 - 25/5", "-$120.50", R.drawable.ic_groceries_gd, false));
        transactions.add(new TransactionItem_gd("Tiền Điện", "10:15 - 15/5", "-$48.30", R.drawable.ic_tax_gd, false));
        transactions.add(new TransactionItem_gd("Tiền Nước", "10:10 - 15/5", "-$25.75", R.drawable.ic_tax_gd, false));
        transactions.add(new TransactionItem_gd("Phương Tiện", "8:20 - 10/5", "-$15.60", R.drawable.ic_transportation_gd, false));
        transactions.add(new TransactionItem_gd("Ăn Uống", "19:45 - 5/5", "-$35.25", R.drawable.ic_food_gd, false));

        // Tháng Tư
        transactions.add(new TransactionItem_gd("Tháng Tư", true));
        transactions.add(new TransactionItem_gd("Lương", "18:27 - 30/4", "$4,000.00", R.drawable.ic_salary_gd, false));
        transactions.add(new TransactionItem_gd("Tạp Hóa", "17:00 - 24/4", "-$100.00", R.drawable.ic_groceries_gd, false));
        transactions.add(new TransactionItem_gd("Thuế", "8:30 - 15/4", "-$674.40", R.drawable.ic_tax_gd, false));
        transactions.add(new TransactionItem_gd("Phương Tiện", "7:30 - 8/4", "-$4.13", R.drawable.ic_transportation_gd, false));

        // Tháng Ba
        transactions.add(new TransactionItem_gd("Tháng Ba", true));
        transactions.add(new TransactionItem_gd("Thực Phẩm", "19:30 - 31/3", "-$70.40", R.drawable.ic_food_gd, false));
        transactions.add(new TransactionItem_gd("Lương", "15:45 - 28/3", "$3,800.00", R.drawable.ic_salary_gd, false));
        transactions.add(new TransactionItem_gd("Mua Sắm", "14:20 - 20/3", "-$250.30", R.drawable.ic_groceries_gd, false));
        transactions.add(new TransactionItem_gd("Tiền Điện", "9:15 - 15/3", "-$45.75", R.drawable.ic_tax_gd, false));
        transactions.add(new TransactionItem_gd("Tiền Nước", "9:10 - 15/3", "-$22.50", R.drawable.ic_tax_gd, false));

        // Tháng Hai
        transactions.add(new TransactionItem_gd("Tháng Hai", true));
        transactions.add(new TransactionItem_gd("Lương", "16:30 - 28/2", "$3,800.00", R.drawable.ic_salary_gd, false));
        transactions.add(new TransactionItem_gd("Thực Phẩm", "18:45 - 25/2", "-$85.20", R.drawable.ic_food_gd, false));
        transactions.add(new TransactionItem_gd("Phương Tiện", "7:30 - 20/2", "-$12.50", R.drawable.ic_transportation_gd, false));
        transactions.add(new TransactionItem_gd("Mua Sắm", "13:15 - 15/2", "-$120.75", R.drawable.ic_groceries_gd, false));


        adapter.setTransactions(transactions);
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
                    adapter.setTransactions(currentList);
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