package org.o7planning.nhom8_quanlychitieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.models.Goal;
import org.o7planning.nhom8_quanlychitieu.utils.CurrencyFormatter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_GOAL = 0;
    private static final int VIEW_TYPE_ADD = 1;

    private Context context;
    private List<Goal> goals;
    private OnGoalClickListener listener;
    private CurrencyFormatter currencyFormatter;

    public GoalAdapter(Context context, List<Goal> goals, OnGoalClickListener listener) {
        this.context = context;
        this.goals = goals;
        this.listener = listener;
        this.currencyFormatter = new CurrencyFormatter();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_add_goal, parent, false);
            return new AddGoalViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false);
            return new GoalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoalViewHolder) {
            GoalViewHolder goalHolder = (GoalViewHolder) holder;
            Goal goal = goals.get(position);

            goalHolder.tvGoalName.setText(goal.getName());

            // Format currency
            String targetAmount = currencyFormatter.formatCurrency(goal.getTargetAmount());
            String currentAmount = currencyFormatter.formatCurrency(goal.getCurrentAmount());
            goalHolder.tvGoalAmount.setText(currentAmount + " / " + targetAmount);

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String targetDate = sdf.format(goal.getTargetDate());
            goalHolder.tvGoalDate.setText("Đến: " + targetDate);

            // Set progress
            float progress = goal.getProgressPercentage();
            goalHolder.progressBar.setProgress((int) progress);
            goalHolder.tvProgress.setText(String.format("%.1f%%", progress));

            // Set color based on progress
            if (progress >= 100) {
                goalHolder.progressBar.setProgressTintList(context.getColorStateList(R.color.gd_income_green));
                goalHolder.tvProgress.setTextColor(context.getColor(R.color.gd_income_green));
            } else if (progress >= 50) {
                goalHolder.progressBar.setProgressTintList(context.getColorStateList(R.color.gd_income_green));
                goalHolder.tvProgress.setTextColor(context.getColor(R.color.gd_income_green));
            } else {
                goalHolder.progressBar.setProgressTintList(context.getColorStateList(R.color.gd_expense_red));
                goalHolder.tvProgress.setTextColor(context.getColor(R.color.gd_expense_red));
            }

            // Set click listener
            goalHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGoalClick(goal);
                }
            });

            // Set delete listener
            goalHolder.ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteGoalClick(goal);
                }
            });
        } else if (holder instanceof AddGoalViewHolder) {
            AddGoalViewHolder addHolder = (AddGoalViewHolder) holder;
            addHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddGoalClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return goals.size() + 1; // +1 for add button
    }

    @Override
    public int getItemViewType(int position) {
        if (position == goals.size()) {
            return VIEW_TYPE_ADD;
        } else {
            return VIEW_TYPE_GOAL;
        }
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName, tvGoalAmount, tvGoalDate, tvProgress;
        ProgressBar progressBar;
        ImageView ivDelete;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tv_goal_name);
            tvGoalAmount = itemView.findViewById(R.id.tv_goal_amount);
            tvGoalDate = itemView.findViewById(R.id.tv_goal_date);
            tvProgress = itemView.findViewById(R.id.tv_progress);
            progressBar = itemView.findViewById(R.id.progress_bar);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    public static class AddGoalViewHolder extends RecyclerView.ViewHolder {
        public AddGoalViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
        void onAddGoalClick();
        void onDeleteGoalClick(Goal goal);
    }
}
