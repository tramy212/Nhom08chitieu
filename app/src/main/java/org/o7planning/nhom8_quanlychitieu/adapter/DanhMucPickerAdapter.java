package org.o7planning.nhom8_quanlychitieu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;

import java.util.List;

public class DanhMucPickerAdapter extends RecyclerView.Adapter<DanhMucPickerAdapter.DanhMucViewHolder> {

    private List<DanhMucModel> danhMucList;
    private OnItemClickListener listener;
    private Context context;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(DanhMucModel danhMuc);
    }

    public DanhMucPickerAdapter(Context context, List<DanhMucModel> danhMucList, OnItemClickListener listener) {
        this.context = context;
        this.danhMucList = danhMucList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DanhMucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danh_muc, parent, false);
        return new DanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhMucViewHolder holder, int position) {
        DanhMucModel danhMuc = danhMucList.get(position);
        holder.textViewTenDanhMuc.setText(danhMuc.getTen());

        // Thiết lập màu nền và icon
        try {
            // Thiết lập màu nền từ danhMuc
            String mauSac = danhMuc.getMauSac();
            if (mauSac != null && !mauSac.isEmpty()) {
                holder.iconContainer.setBackgroundColor(Color.parseColor(mauSac));
            } else {
                holder.iconContainer.setBackgroundColor(Color.parseColor("#87CEFA")); // Màu mặc định
            }
        } catch (Exception e) {
            // Nếu có lỗi khi parse màu, sử dụng màu mặc định
            holder.iconContainer.setBackgroundColor(Color.parseColor("#87CEFA"));
        }

        // Thiết lập icon dựa trên loại danh mục
        setIconForCategory(holder.iconView, danhMuc.getTen(), danhMuc.getIcon());

        // Ẩn nút xóa
        if (holder.deleteButton != null) {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Hiển thị trạng thái đã chọn
        if (position == selectedPosition) {
            holder.cardViewIcon.setCardBackgroundColor(Color.parseColor("#4FC3F7"));
            holder.textViewTenDanhMuc.setTextColor(Color.parseColor("#4FC3F7"));
        } else {
            holder.cardViewIcon.setCardBackgroundColor(Color.WHITE);
            holder.textViewTenDanhMuc.setTextColor(Color.BLACK);
        }

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemClick(danhMuc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }

    private void setIconForCategory(ImageView iconView, String categoryName, String iconType) {
        // Nếu có icon type cụ thể, sử dụng nó
        if (iconType != null && !iconType.isEmpty() && !"null".equals(iconType)) {
            switch (iconType) {
                case "spa":
                    iconView.setImageResource(R.drawable.ic_spa);
                    return;
                case "pet":
                    iconView.setImageResource(R.drawable.ic_pet);
                    return;
                case "travel":
                    iconView.setImageResource(R.drawable.ic_travel);
                    return;
                case "bill":
                    iconView.setImageResource(R.drawable.ic_bill);
                    return;
                case "fashion":
                    iconView.setImageResource(R.drawable.ic_fashion);
                    return;
                case "drink":
                    iconView.setImageResource(R.drawable.ic_drink);
                    return;
                case "gift":
                    iconView.setImageResource(R.drawable.ic_gift);
                    return;
                case "shopping":
                    iconView.setImageResource(R.drawable.ic_shopping);
                    return;
                case "investment":
                    iconView.setImageResource(R.drawable.ic_investment);
                    return;
                case "education":
                    iconView.setImageResource(R.drawable.ic_education);
                    return;
                case "skincare":
                    iconView.setImageResource(R.drawable.ic_skincare);
                    return;
                case "fuel":
                    iconView.setImageResource(R.drawable.ic_fuel);
                    return;
                case "salary":
                    iconView.setImageResource(R.drawable.ic_salary_gd);
                    return;
                case "default":
                    iconView.setImageResource(R.drawable.ic_dashboard_black_24dp);
                    return;
            }
        }

        // Nếu không có icon type hoặc không khớp, sử dụng tên danh mục để xác định icon
        switch (categoryName.toLowerCase()) {
            case "ăn uống":
                iconView.setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
                break;
            case "di chuyển":
                iconView.setImageResource(R.drawable.ic_directions_car_black_24dp);
                break;
            case "mua sắm":
                iconView.setImageResource(R.drawable.ic_shopping);
                break;
            case "giải trí":
                iconView.setImageResource(R.drawable.ic_local_movies_black_24dp);
                break;
            case "sức khỏe":
                iconView.setImageResource(R.drawable.ic_local_hospital_black_24dp);
                break;
            case "spa":
                iconView.setImageResource(R.drawable.ic_spa);
                break;
            case "thú cưng":
                iconView.setImageResource(R.drawable.ic_pet);
                break;
            case "du lịch":
                iconView.setImageResource(R.drawable.ic_travel);
                break;
            case "hóa đơn điện":
                iconView.setImageResource(R.drawable.ic_bill);
                break;
            case "thời trang":
                iconView.setImageResource(R.drawable.ic_fashion);
                break;
            case "chế độ uống":
                iconView.setImageResource(R.drawable.ic_drink);
                break;
            case "quà":
                iconView.setImageResource(R.drawable.ic_gift);
                break;
            case "shopee":
                iconView.setImageResource(R.drawable.ic_shopping);
                break;
            case "đầu tư":
                iconView.setImageResource(R.drawable.ic_investment);
                break;
            case "học tập":
                iconView.setImageResource(R.drawable.ic_education);
                break;
            case "skincare":
                iconView.setImageResource(R.drawable.ic_skincare);
                break;
            case "xăng":
                iconView.setImageResource(R.drawable.ic_fuel);
                break;
            case "lương":
                iconView.setImageResource(R.drawable.ic_salary_gd);
                break;
            default: // Danh mục tùy chỉnh sẽ rơi vào đây
                iconView.setImageResource(R.drawable.ic_dashboard_black_24dp);
                break;
        }
    }

    public DanhMucModel getSelectedDanhMuc() {
        if (selectedPosition >= 0 && selectedPosition < danhMucList.size()) {
            return danhMucList.get(selectedPosition);
        }
        return null;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTenDanhMuc;
        FrameLayout iconContainer;
        ImageView iconView;
        TextView deleteButton;
        CardView cardViewIcon;

        public DanhMucViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTenDanhMuc = itemView.findViewById(R.id.textViewTenDanhMuc);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            iconView = itemView.findViewById(R.id.iconView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            cardViewIcon = itemView.findViewById(R.id.cardViewIcon);
        }
    }
}
