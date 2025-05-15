package org.o7planning.nhom8_quanlychitieu.ui.HoSo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.o7planning.nhom8_quanlychitieu.R;

public class PasswordSettingsFragment extends Fragment {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private TextView tvForgotPassword;
    private ImageView backButton;
    // Đã xóa khai báo btnBackToSettings

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_password_settings, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etCurrentPassword = root.findViewById(R.id.etCurrentPassword);
        etNewPassword = root.findViewById(R.id.etNewPassword);
        etConfirmPassword = root.findViewById(R.id.etConfirmPassword);
        btnChangePassword = root.findViewById(R.id.btnChangePassword);
        tvForgotPassword = root.findViewById(R.id.tvForgotPassword);
        backButton = root.findViewById(R.id.backButton);
        // Đã xóa dòng tham chiếu đến btnBackToSettings

        // Set click listeners
        btnChangePassword.setOnClickListener(v -> {
            changePassword();
        });

        tvForgotPassword.setOnClickListener(v -> {
            sendPasswordResetEmail();
        });

        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Đã xóa click listener cho btnBackToSettings

        return root;
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 8) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            return;
        }

        if (!isValidPassword(newPassword)) {
            etNewPassword.setError("Mật khẩu phải có ít nhất một chữ hoa, một chữ thường và một số");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Re-authenticate user
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Change password
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getContext(), "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                            requireActivity().onBackPressed();
                                        } else {
                                            Toast.makeText(getContext(), "Lỗi khi thay đổi mật khẩu: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Xác thực thất bại. Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isValidPassword(String password) {
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUpperCase && hasLowerCase && hasDigit;
    }

    private void sendPasswordResetEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            mAuth.sendPasswordResetEmail(user.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Email đặt lại mật khẩu đã được gửi đến " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi gửi email đặt lại mật khẩu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Không thể xác định email người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}