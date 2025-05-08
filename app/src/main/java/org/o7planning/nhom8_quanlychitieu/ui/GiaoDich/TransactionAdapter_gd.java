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

    public TransactionAdapter_gd(Context context) {
        this.context = context;
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
        } else if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder transactionHolder = (TransactionViewHolder) holder;
            transactionHolder.bind(item);
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

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonthYear;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonthYear = itemView.findViewById(R.id.tv_month_year);
        }

        public void bind(String monthYear) {
            tvMonthYear.setText(monthYear);
        }
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryIcon;
        private final TextView tvTransactionName;
        private final TextView tvDate;
        private final TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvTransactionName = itemView.findViewById(R.id.tv_transaction_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }

        public void bind(TransactionItem_gd transaction) {
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