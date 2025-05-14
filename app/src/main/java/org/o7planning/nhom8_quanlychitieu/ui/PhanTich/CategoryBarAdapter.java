package org.o7planning.nhom8_quanlychitieu.ui.PhanTich;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.utils.CurrencyFormatter;

import java.util.List;

public class CategoryBarAdapter extends RecyclerView.Adapter<CategoryBarAdapter.ViewHolder> {

    private Context context;
    private List<CategoryBarItem> items;
    private CurrencyFormatter currencyFormatter;

    public CategoryBarAdapter(Context context, List<CategoryBarItem> items) {
        this.context = context;
        this.items = items;
        this.currencyFormatter = new CurrencyFormatter();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_bar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryBarItem item = items.get(position);

        // Set category name
        holder.tvCategoryName.setText(item.getCategoryName());

        // Set amount
        String formattedAmount = currencyFormatter.formatCurrency(item.getAmount());
        holder.tvAmount.setText(formattedAmount);

        // Set bar progress
        ViewGroup.LayoutParams params = holder.barProgress.getLayoutParams();
        params.width = (int) (holder.barContainer.getWidth() * (item.getPercentage() / 100));
        holder.barProgress.setLayoutParams(params);

        // Set bar color
        holder.barProgress.setBackgroundResource(item.getColorResId());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvAmount;
        View barContainer;
        View barProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            barContainer = itemView.findViewById(R.id.bar_container);
            barProgress = itemView.findViewById(R.id.bar_progress);

            // We need to wait for the container to be laid out before setting the progress width
            barContainer.post(() -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    CategoryBarItem item = items.get(getAdapterPosition());
                    ViewGroup.LayoutParams params = barProgress.getLayoutParams();
                    params.width = (int) (barContainer.getWidth() * (item.getPercentage() / 100));
                    barProgress.setLayoutParams(params);
                }
            });
        }
    }
}
