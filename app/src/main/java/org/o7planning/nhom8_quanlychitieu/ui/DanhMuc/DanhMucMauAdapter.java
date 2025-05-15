package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;

import java.util.List;

public class DanhMucMauAdapter extends RecyclerView.Adapter<DanhMucMauAdapter.DanhMucMauViewHolder> {

    private List<DanhMucMauModel> danhMucMauList;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(DanhMucMauModel danhMucMau);
    }

    public DanhMucMauAdapter(Context context, List<DanhMucMauModel> danhMucMauList, OnItemClickListener listener) {
        this.context = context;
        this.danhMucMauList = danhMucMauList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DanhMucMauViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danh_muc_mau, parent, false);
        return new DanhMucMauViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhMucMauViewHolder holder, int position) {
        DanhMucMauModel danhMucMau = danhMucMauList.get(position);
        holder.textViewTenDanhMuc.setText(danhMucMau.getTen());

        // Set background color
        try {
            holder.iconContainer.setBackgroundColor(Color.parseColor("#87CEFA")); // Light blue for all template categories
        } catch (Exception e) {
            holder.iconContainer.setBackgroundColor(Color.parseColor("#87CEFA"));
        }

        // Set icon based on category type
        setIconForCategory(holder.iconView, danhMucMau.getIcon());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(danhMucMau);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucMauList.size();
    }

    private void setIconForCategory(ImageView iconView, String iconType) {
        switch (iconType) {
            case "spa":
                iconView.setImageResource(R.drawable.ic_spa);
                break;
            case "pet":
                iconView.setImageResource(R.drawable.ic_pet); // Sửa để dùng đúng icon cho Thú Cưng
                break;
            case "travel":
                iconView.setImageResource(R.drawable.ic_travel);
                break;
            case "bill":
                iconView.setImageResource(R.drawable.ic_bill);
                break;
            case "fashion":
                iconView.setImageResource(R.drawable.ic_fashion);
                break;
            case "drink":
                iconView.setImageResource(R.drawable.ic_drink);
                break;
            case "gift":
                iconView.setImageResource(R.drawable.ic_gift);
                break;
            case "shopping":
                iconView.setImageResource(R.drawable.ic_groceries_gd);
                break;
            case "investment":
                iconView.setImageResource(R.drawable.ic_investment);
                break;
            case "education":
                iconView.setImageResource(R.drawable.ic_education);
                break;
            case "skincare":
                iconView.setImageResource(R.drawable.ic_skincare);
                break;
            case "fuel":
                iconView.setImageResource(R.drawable.ic_fuel);
                break;
            case "salary":
                iconView.setImageResource(R.drawable.ic_salary_gd);
                break;
            default:
                iconView.setImageResource(R.drawable.ic_dashboard_black_24dp);
                break;
        }
    }

    public static class DanhMucMauViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTenDanhMuc;
        FrameLayout iconContainer;
        ImageView iconView;

        public DanhMucMauViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTenDanhMuc = itemView.findViewById(R.id.textViewTenDanhMuc);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            iconView = itemView.findViewById(R.id.iconView);
        }
    }
}