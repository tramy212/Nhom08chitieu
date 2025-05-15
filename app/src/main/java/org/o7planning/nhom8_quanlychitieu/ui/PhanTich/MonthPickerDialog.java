package org.o7planning.nhom8_quanlychitieu.ui.PhanTich;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.o7planning.nhom8_quanlychitieu.R;

public class MonthPickerDialog extends Dialog {

    public interface OnMonthSelectedListener {
        void onMonthSelected(int month, int year);
    }

    private int selectedMonth;
    private int selectedYear;
    private final OnMonthSelectedListener listener;
    private TextView[] monthButtons;

    public MonthPickerDialog(Context context, int initialMonth, int initialYear, OnMonthSelectedListener listener) {
        super(context);
        this.selectedMonth = initialMonth;
        this.selectedYear = initialYear;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_month_picker);

        // Thiết lập kích thước và vị trí của dialog
        Window window = getWindow();
        if (window != null) {
            // Đặt background trong suốt để hiển thị bo góc của layout
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Thiết lập kích thước và vị trí
            WindowManager.LayoutParams params = window.getAttributes();

            // Đặt kích thước tối thiểu để đảm bảo dialog đủ lớn
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.85);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            // Đảm bảo dialog hiển thị ở giữa màn hình
            params.gravity = Gravity.CENTER;

            window.setAttributes(params);

            // Đảm bảo dialog không bị lệch
            window.setLayout(params.width, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        // Get views
        TextView tvYear = findViewById(R.id.tv_year);
        ImageView ivPrevYear = findViewById(R.id.iv_prev_year);
        ImageView ivNextYear = findViewById(R.id.iv_next_year);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnOk = findViewById(R.id.btn_ok);

        // Set initial year
        tvYear.setText(String.valueOf(selectedYear));

        // Setup month buttons
        monthButtons = new TextView[12];
        for (int i = 1; i <= 12; i++) {
            int resId = getContext().getResources().getIdentifier("month_" + i, "id", getContext().getPackageName());
            monthButtons[i-1] = findViewById(resId);

            final int month = i;
            monthButtons[i-1].setOnClickListener(v -> {
                // Reset all buttons
                for (TextView btn : monthButtons) {
                    btn.setBackgroundResource(R.drawable.month_background);
                    btn.setTextColor(Color.parseColor("#757575"));
                }

                // Highlight selected button
                v.setBackgroundResource(R.drawable.month_selected_background);
                ((TextView) v).setTextColor(Color.parseColor("#2196F3"));

                // Update selected month
                selectedMonth = month;
            });

            // Highlight current selection
            if (i == selectedMonth) {
                monthButtons[i-1].setBackgroundResource(R.drawable.month_selected_background);
                monthButtons[i-1].setTextColor(Color.parseColor("#2196F3"));
            } else {
                monthButtons[i-1].setBackgroundResource(R.drawable.month_background);
                monthButtons[i-1].setTextColor(Color.parseColor("#757575"));
            }
        }

        // Setup year navigation
        ivPrevYear.setOnClickListener(v -> {
            int year = Integer.parseInt(tvYear.getText().toString()) - 1;
            tvYear.setText(String.valueOf(year));
            selectedYear = year;
        });

        ivNextYear.setOnClickListener(v -> {
            int year = Integer.parseInt(tvYear.getText().toString()) + 1;
            tvYear.setText(String.valueOf(year));
            selectedYear = year;
        });

        // Setup buttons
        btnCancel.setOnClickListener(v -> dismiss());

        btnOk.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMonthSelected(selectedMonth, selectedYear);
            }
            dismiss();
        });
    }

    @Override
    public void show() {
        super.show();
        // Đảm bảo dialog hiển thị ở giữa màn hình sau khi show
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }
    }
}