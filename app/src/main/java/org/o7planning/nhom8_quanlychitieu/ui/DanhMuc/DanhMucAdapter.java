package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.List;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.DanhMucViewHolder> {

    private List<DanhMucModel> danhMucList;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(DanhMucModel danhMuc);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public DanhMucAdapter(Context context, List<DanhMucModel> danhMucList,
                          OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        this.context = context;
        this.danhMucList = danhMucList;
        this.listener = listener;
        this.deleteListener = deleteListener;
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

        // Thiết lập màu nền và icon dựa trên loại danh mục
        if ("Thêm".equals(danhMuc.getTen())) {
            // Nút "Thêm"
            holder.iconContainer.setBackgroundColor(Color.parseColor("#87CEFA")); // Màu xanh nhạt
            holder.iconView.setImageResource(R.drawable.ic_add_circle);
            holder.deleteButton.setVisibility(View.GONE); // Ẩn nút xóa cho nút "Thêm"
        } else {
            // Danh mục thông thường
            holder.iconContainer.setBackgroundColor(Color.parseColor("#87CEFA")); // Màu xanh nhạt
            // Thiết lập icon dựa trên loại danh mục
            setIconForCategory(holder.iconView, danhMuc.getTen());
            holder.deleteButton.setVisibility(View.VISIBLE); // Hiển thị nút xóa cho danh mục thông thường
        }

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(danhMuc);
            }
        });

        // Thiết lập sự kiện click cho nút xóa với hộp thoại xác nhận
        final int itemPosition = position;
        holder.deleteButton.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận trước khi xóa
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này không?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (deleteListener != null) {
                        deleteListener.onDeleteClick(itemPosition);
                    }
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }

    private void setIconForCategory(ImageView iconView, String categoryName) {
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
                iconView.setImageResource(R.drawable.ic_salary_gd); // Use the new icon
                break;

            default: // Danh mục tùy chỉnh sẽ rơi vào đây
                iconView.setImageResource(R.drawable.ic_dashboard_black_24dp);
                break;
        }
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