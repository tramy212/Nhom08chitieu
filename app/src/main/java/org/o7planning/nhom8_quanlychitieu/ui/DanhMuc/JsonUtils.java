package org.o7planning.nhom8_quanlychitieu.ui.DanhMuc;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.ui.DanhMuc.DanhMucModel;

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

                DanhMucModel danhMuc = new DanhMucModel(id, ten, moTa);
                danhMucList.add(danhMuc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return danhMucList;
    }
}