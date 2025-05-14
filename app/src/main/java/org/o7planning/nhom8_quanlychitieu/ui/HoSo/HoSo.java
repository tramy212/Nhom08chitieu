package org.o7planning.nhom8_quanlychitieu.ui.HoSo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HoSo extends Fragment {

    private static final String TAG = "HoSo";
    private TextView userName, userEmail;
    private Button editProfileBtn;
    private LinearLayout notificationSettings, passwordSettings, termsSettings;
    private LinearLayout logoutBtn;
    private ImageView notificationBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userKey; // Để lưu key của user trong database
    private CircleImageView profileImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ho_so, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        userName = root.findViewById(R.id.userName);
        userEmail = root.findViewById(R.id.userEmail);
        editProfileBtn = root.findViewById(R.id.editProfileBtn);
        notificationSettings = root.findViewById(R.id.notificationSettings);
        passwordSettings = root.findViewById(R.id.passwordSettings);
        termsSettings = root.findViewById(R.id.termsSettings);
        logoutBtn = root.findViewById(R.id.logoutBtn);
        notificationBtn = root.findViewById(R.id.notificationBtn);
        profileImage = root.findViewById(R.id.profileImage);

        // Load user data
        loadUserData();

        // Set click listeners
        editProfileBtn.setOnClickListener(v -> {
            showEditProfileDialog();
        });

        notificationSettings.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_hoso_to_notificationSettings);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                Toast.makeText(getContext(), "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        passwordSettings.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_hoso_to_passwordSettings);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                Toast.makeText(getContext(), "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        termsSettings.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_hoso_to_termsSettings);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                Toast.makeText(getContext(), "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        logoutBtn.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_hoso_to_logoutConfirmation);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                // Nếu không thể navigate đến màn hình xác nhận, đăng xuất trực tiếp
                logoutDirectly();
            }
        });

        return root;
    }

    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_profile);

        // Thiết lập dialog full width và có background trong suốt
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);

        // Get dialog views
        TextInputEditText etUserName = dialog.findViewById(R.id.et_user_name);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        CircleImageView profileImageEdit = dialog.findViewById(R.id.profile_image_edit);

        // Set current user name
        etUserName.setText(userName.getText().toString());

        // Setup save button
        btnSave.setOnClickListener(v -> {
            String newName = etUserName.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                etUserName.setError("Vui lòng nhập tên");
                return;
            }

            updateUserName(newName);
            dialog.dismiss();
        });

        // Setup cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Setup close button
        ivClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateUserName(String newName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && userKey != null) {
            // Hiển thị loading
            Toast.makeText(getContext(), "Đang cập nhật...", Toast.LENGTH_SHORT).show();

            // Update name in Firebase
            mDatabase.child("Users").child(userKey).child("name").setValue(newName)
                    .addOnSuccessListener(aVoid -> {
                        // Update UI
                        userName.setText(newName);
                        Toast.makeText(getContext(), "Tên đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Không thể cập nhật thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutDirectly() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        // Chuyển về màn hình đăng nhập
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_hoso_to_dangnhap);
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: ", e);
            // Nếu không thể navigate trực tiếp, thử cách khác
            try {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.dangnhap);
            } catch (Exception ex) {
                Log.e(TAG, "Second navigation error: ", ex);
                Toast.makeText(getContext(), "Không thể chuyển về màn hình đăng nhập. Vui lòng khởi động lại ứng dụng.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set email from Firebase Auth
            userEmail.setText(currentUser.getEmail());

            // Get additional user data from Realtime Database
            mDatabase.child("Users").orderByChild("email").equalTo(currentUser.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Lưu key của user để sử dụng khi cập nhật
                                    userKey = snapshot.getKey();

                                    String name = snapshot.child("name").getValue(String.class);
                                    if (name != null && !name.isEmpty()) {
                                        userName.setText(name);
                                    } else {
                                        // Nếu không có tên, hiển thị email hoặc "Người dùng"
                                        String displayName = currentUser.getDisplayName();
                                        if (displayName != null && !displayName.isEmpty()) {
                                            userName.setText(displayName);
                                        } else {
                                            userName.setText("Người dùng");
                                        }
                                    }
                                }
                            } else {
                                // Nếu không tìm thấy dữ liệu trong database, hiển thị thông tin từ FirebaseUser
                                String displayName = currentUser.getDisplayName();
                                if (displayName != null && !displayName.isEmpty()) {
                                    userName.setText(displayName);
                                } else {
                                    userName.setText("Người dùng");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Database error: " + databaseError.getMessage());
                            Toast.makeText(getContext(), "Lỗi khi tải thông tin người dùng: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            // Nếu có lỗi, hiển thị thông tin từ FirebaseUser
                            String displayName = currentUser.getDisplayName();
                            if (displayName != null && !displayName.isEmpty()) {
                                userName.setText(displayName);
                            } else {
                                userName.setText("Người dùng");
                            }
                        }
                    });
        } else {
            // Nếu người dùng chưa đăng nhập, chuyển về màn hình đăng nhập
            try {
                Navigation.findNavController(requireView()).navigate(R.id.action_hoso_to_dangnhap);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error in loadUserData: ", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra lại trạng thái đăng nhập mỗi khi fragment được hiển thị
        if (mAuth.getCurrentUser() == null) {
            try {
                Navigation.findNavController(requireView()).navigate(R.id.action_hoso_to_dangnhap);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error in onResume: ", e);
            }
        }
    }
}
