package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class GiaoDichFragment_gd extends Fragment {
    private TextView tvTotalBalance;
    private TextView tvIncome;
    private TextView tvExpense;
    private RecyclerView rvTransactions;
    private TransactionAdapter_gd adapter;
    private Toolbar toolbar;
    private ImageView ivBack;
    private ImageView ivNotification;

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
        rvTransactions.setAdapter(adapter);

        // Load sample data
        loadSampleData();

        return root;
    }

    private void loadSampleData() {
        // Set summary data
        tvTotalBalance.setText("$7,783.00");
        tvIncome.setText("$4,120.00");
        tvExpense.setText("$1,187.40");

        // Create sample transactions
        List<TransactionItem_gd> transactions = new ArrayList<>();

        // Tháng Tư
        transactions.add(new TransactionItem_gd("Tháng Tư", true));
        transactions.add(new TransactionItem_gd("Lương", "18:27 - 30/4", "$4,000.00", R.drawable.ic_salary_gd, false));
        transactions.add(new TransactionItem_gd("Tạp Hóa", "17:00 - 24/4", "-$100.00", R.drawable.ic_groceries_gd, false));
        transactions.add(new TransactionItem_gd("Thuế", "8:30 - 15/4", "-$674.40", R.drawable.ic_tax_gd, false));
        transactions.add(new TransactionItem_gd("Phương Tiện", "7:30 - 8/4", "-$4.13", R.drawable.ic_transportation_gd, false));

        // Tháng Ba
        transactions.add(new TransactionItem_gd("Tháng Ba", true));
        transactions.add(new TransactionItem_gd("Thực Phẩm", "19:30 - 31/3", "-$70.40", R.drawable.ic_food_gd, false));

        adapter.setTransactions(transactions);
    }
}