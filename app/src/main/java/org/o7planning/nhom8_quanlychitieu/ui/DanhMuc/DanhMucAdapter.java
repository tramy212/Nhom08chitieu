package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;

import java.util.List;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.DanhMucViewHolder> {

    private List<DanhMucModel> danhMucList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DanhMucModel danhMuc);
    }

    public DanhMucAdapter(List<DanhMucModel> danhMucList, OnItemClickListener listener) {
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
        holder.textViewMoTaDanhMuc.setText(danhMuc.getMoTa());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(danhMuc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }

    public static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTenDanhMuc;
        TextView textViewMoTaDanhMuc;

        public DanhMucViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTenDanhMuc = itemView.findViewById(R.id.textViewTenDanhMuc);
            textViewMoTaDanhMuc = itemView.findViewById(R.id.textViewMoTaDanhMuc);
        }
    }
}