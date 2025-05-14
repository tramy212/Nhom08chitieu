package org.o7planning.nhom8_quanlychitieu.ui.TrangChu;

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

import java.util.List;

public class HomeTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_TRANSACTION = 1;

    private final Context context;
    private final List<HomeTransactionItem> transactions;

    public HomeTransactionAdapter(Context context, List<HomeTransactionItem> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_transaction_home, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeTransactionItem item = transactions.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.bind(item.getDate());
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
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_TRANSACTION;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tv_date_header);
        }

        public void bind(String date) {
            tvDateHeader.setText(date);
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

        public void bind(HomeTransactionItem transaction) {
            // Set icon
            if (transaction.getIconResource() != 0) {
                ivCategoryIcon.setImageResource(transaction.getIconResource());
            } else {
                // Set icon based on transaction type
                if (transaction.getType().equals("income")) {
                    ivCategoryIcon.setImageResource(R.drawable.ic_income_gd);
                } else {
                    ivCategoryIcon.setImageResource(R.drawable.ic_expense_gd);
                }
            }

            // Set background color for icon
            if (transaction.getType().equals("income")) {
                ivCategoryIcon.setBackgroundResource(R.drawable.circle_background_blue_gd);
            } else {
                ivCategoryIcon.setBackgroundResource(R.drawable.circle_background_red_gd);
            }

            // Set amount color
            if (transaction.getType().equals("income")) {
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.gd_income_green));
            } else {
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.gd_expense_red));
            }

            // Set transaction name and date
            tvTransactionName.setText(transaction.getName());

            // Format date to show only day and month
            String formattedDate = formatDate(transaction.getDate());
            tvDate.setText(formattedDate);

            // Set amount
            tvAmount.setText(transaction.getAmount());
        }

        private String formatDate(String fullDate) {
            // Convert from "dd/MM/yyyy" to "dd/MM"
            if (fullDate != null && fullDate.length() >= 5) {
                return fullDate.substring(0, 5);
            }
            return fullDate;
        }
    }
}
