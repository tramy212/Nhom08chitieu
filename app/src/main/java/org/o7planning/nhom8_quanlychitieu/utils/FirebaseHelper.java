package org.o7planning.nhom8_quanlychitieu.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    /**
     * Phương thức kiểm tra kết nối với Firebase
     * Ghi một giá trị vào nút "test" và đọc lại để xác nhận kết nối
     */
    public static void testConnection() {
        // Lấy instance của Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Lấy reference đến nút "test"
        DatabaseReference testRef = database.getReference("test");

        // Ghi một giá trị vào nút "test"
        testRef.setValue("Hello Firebase! " + System.currentTimeMillis())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Ghi dữ liệu thành công!");

                    // Đọc giá trị vừa ghi để xác nhận
                    readTestValue(testRef);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi ghi dữ liệu: " + e.getMessage());
                });
    }

    /**
     * Phương thức đọc giá trị từ nút "test"
     */
    private static void readTestValue(DatabaseReference testRef) {
        testRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Log.d(TAG, "Đọc dữ liệu thành công: " + value);
                Log.d(TAG, "Kết nối Firebase hoạt động tốt!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi đọc dữ liệu: " + error.getMessage());
            }
        });
    }
}