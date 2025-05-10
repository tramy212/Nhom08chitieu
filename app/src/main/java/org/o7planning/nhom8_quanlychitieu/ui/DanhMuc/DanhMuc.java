package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.JsonUtils;

import java.util.List;

public class DanhMuc extends Fragment implements DanhMucAdapter.OnItemClickListener {

    private RecyclerView recyclerViewDanhMuc;
    private DanhMucAdapter danhMucAdapter;
    private List<DanhMucModel> danhMucList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_danh_muc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView
        recyclerViewDanhMuc = view.findViewById(R.id.recyclerViewDanhMuc);
        recyclerViewDanhMuc.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải dữ liệu từ JSON
        loadDanhMucData();
    }

    private void loadDanhMucData() {
        // Tải JSON từ thư mục raw thay vì assets
        String jsonString = JsonUtils.loadJSONFromRaw(getContext());

        if (jsonString != null) {
            danhMucList = JsonUtils.parseDanhMucFromJson(jsonString);
            danhMucAdapter = new DanhMucAdapter(danhMucList, this);
            recyclerViewDanhMuc.setAdapter(danhMucAdapter);
        } else {
            Toast.makeText(getContext(), "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(DanhMucModel danhMuc) {
        // Xử lý khi nhấp vào mục
        Toast.makeText(getContext(), "Đã chọn: " + danhMuc.getTen(), Toast.LENGTH_SHORT).show();

        // Bạn có thể điều hướng đến fragment khác hoặc hiển thị chi tiết ở đây
        // Ví dụ:
        // Bundle bundle = new Bundle();
        // bundle.putInt("danhMucId", danhMuc.getId());
        // Navigation.findNavController(getView()).navigate(R.id.action_danhMuc_to_danhMucDetail, bundle);
    }
}