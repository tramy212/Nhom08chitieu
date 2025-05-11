
package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.nhom8_quanlychitieu.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static String loadJSONFromRaw(Context context) {
        String json;
        try {
            // Tải file từ thư mục raw
            InputStream is = context.getResources().openRawResource(R.raw.expense_data_with_danhmuc);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static List<DanhMucModel> parseDanhMucFromJson(String jsonString) {
        List<DanhMucModel> danhMucList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray danhMucArray = jsonObject.getJSONArray("DanhMuc");

            for (int i = 0; i < danhMucArray.length(); i++) {
                JSONObject danhMucObj = danhMucArray.getJSONObject(i);
                int id = danhMucObj.getInt("id");
                String ten = danhMucObj.getString("ten");
                String moTa = danhMucObj.getString("moTa");

                // Try to get icon and color if they exist, otherwise use defaults
                String icon = danhMucObj.has("icon") ? danhMucObj.getString("icon") : null;
                String mauSac = danhMucObj.has("mauSac") ? danhMucObj.getString("mauSac") : null;

                DanhMucModel danhMuc;
                if (icon != null && mauSac != null) {
                    danhMuc = new DanhMucModel(id, ten, moTa, icon, mauSac);
                } else {
                    danhMuc = new DanhMucModel(id, ten, moTa);
                }

                danhMucList.add(danhMuc);
            }

            // Add "Thêm" button as the last item
            DanhMucModel addButton = new DanhMucModel(
                    -1, // Special ID for add button
                    "Thêm",
                    "Thêm danh mục mới",
                    "add",
                    "#87CEFA" // Light blue
            );
            danhMucList.add(addButton);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return danhMucList;
    }
}