package org.o7planning.nhom8_quanlychitieu.ui.GiaoDich;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter_gd extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MONTH = 0;
    private static final int VIEW_TYPE_TRANSACTION = 1;

    private final Context context;
    private List<TransactionItem_gd> transactions = new ArrayList<>();
    private OnTransactionActionListener listener;

    // Interface để xử lý sự kiện
    public interface OnTransactionActionListener {
        void onEditClick(TransactionItem_gd transaction, int position);
        void onDeleteClick(TransactionItem_gd transaction, int position);
        void onCalendarClick(String monthYear);
    }

    public TransactionAdapter_gd(Context context) {
        this.context = context;
    }

    public void setOnTransactionActionListener(OnTransactionActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_MONTH) {
            View view = inflater.inflate(R.layout.item_month_header_gd, parent, false);
            return new MonthViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_transaction_gd, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionItem_gd item = transactions.get(position);

        if (holder instanceof MonthViewHolder) {
            MonthViewHolder monthHolder = (MonthViewHolder) holder;
            monthHolder.bind(item.getName());

            // Chỉ hiển thị lịch ở tháng Tư
            if (item.getName().equals("Tháng Tư")) {
                monthHolder.showCalendar();
            } else {
                monthHolder.hideCalendar();
            }
        } else if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder transactionHolder = (TransactionViewHolder) holder;
            transactionHolder.bind(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (transactions.get(position).isHeader()) {
            return VIEW_TYPE_MONTH;
        } else {
            return VIEW_TYPE_TRANSACTION;
        }
    }

    public void setTransactions(List<TransactionItem_gd> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public List<TransactionItem_gd> getTransactions() {
        return transactions;
    }

    class MonthViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonthYear;
        private final ImageView ivCalendar;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonthYear = itemView.findViewById(R.id.tv_month_year);
            ivCalendar = itemView.findViewById(R.id.iv_calendar);

            // Xử lý sự kiện click vào icon lịch
            ivCalendar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCalendarClick(tvMonthYear.getText().toString());
                }
            });
        }

        public void bind(String monthYear) {
            tvMonthYear.setText(monthYear);
        }

        public void showCalendar() {
            ivCalendar.setVisibility(View.VISIBLE);
        }

        public void hideCalendar() {
            ivCalendar.setVisibility(View.GONE);
        }
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryIcon;
        private final TextView tvTransactionName;
        private final TextView tvDate;
        private final TextView tvAmount;
        private final ImageView ivEdit;
        private final ImageView ivDelete;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvTransactionName = itemView.findViewById(R.id.tv_transaction_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);

            // Xử lý sự kiện click vào nút chỉnh sửa
            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(transactions.get(position), position);
                    }
                }
            });

            // Xử lý sự kiện click vào nút xóa
            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(transactions.get(position), position);
                    }
                }
            });
        }

        public void bind(TransactionItem_gd transaction, int position) {
            // Đặt icon
            if (transaction.getIconResource() != 0) {
                ivCategoryIcon.setImageResource(transaction.getIconResource());
            } else {
                // Đặt icon dựa vào loại giao dịch
                if (transaction.getType().equals("income")) {
                    ivCategoryIcon.setImageResource(R.drawable.ic_income_gd);
                } else {
                    ivCategoryIcon.setImageResource(R.drawable.ic_expense_gd);
                }
            }

            // Đặt màu cho số tiền
            if (transaction.getType().equals("income")) {
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.gd_income_green));
                if (!transaction.getAmount().startsWith("+")) {
                    tvAmount.setText("+" + transaction.getAmount());
                } else {
                    tvAmount.setText(transaction.getAmount());
                }
            } else {
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.gd_expense_red));
                if (!transaction.getAmount().startsWith("-")) {
                    tvAmount.setText("-" + transaction.getAmount());
                } else {
                    tvAmount.setText(transaction.getAmount());
                }
            }

            // Đặt tên và ngày giao dịch
            tvTransactionName.setText(transaction.getName());
            tvTransactionName.setTextColor(ContextCompat.getColor(context, R.color.gd_letters_and_icons));

            tvDate.setText(transaction.getDate());
            tvDate.setTextColor(ContextCompat.getColor(context, R.color.gd_gray));
        }
    }
}