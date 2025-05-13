package org.o7planning.nhom8_quanlychitieu.ui.DangNhap;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import org.o7planning.nhom8_quanlychitieu.R;

public class DangNhap extends Fragment {

    private static final String TAG = "DangNhap";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;
    private ImageView ivTogglePassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private boolean passwordVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dang_nhap, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etEmail = root.findViewById(R.id.etEmail);
        etPassword = root.findViewById(R.id.etPassword);
        btnLogin = root.findViewById(R.id.btnLogin);
        btnRegister = root.findViewById(R.id.btnRegister);
        tvForgotPassword = root.findViewById(R.id.tvForgotPassword);
        ivTogglePassword = root.findViewById(R.id.ivTogglePassword);
        progressBar = root.findViewById(R.id.progressBar);

        // Set click listeners
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> navigateToRegister());
        tvForgotPassword.setOnClickListener(v -> forgotPassword());
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                navigateToHome();
            } else {
                // Người dùng đã đăng nhập nhưng chưa xác thực email
                Toast.makeText(getContext(), "Vui lòng xác thực email trước khi đăng nhập.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email không được để trống");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mật khẩu không được để trống");
            etPassword.requestFocus();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                Toast.makeText(getContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            } else {
                                Toast.makeText(getContext(), "Vui lòng xác thực email trước khi đăng nhập. Kiểm tra hộp thư của bạn.", Toast.LENGTH_LONG).show();
                                // Gửi lại email xác thực nếu cần
                                user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "Email xác thực đã được gửi lại. Vui lòng kiểm tra hộp thư của bạn.", Toast.LENGTH_LONG).show();
                                    }
                                });
                                mAuth.signOut();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.w(TAG, "signInWithEmail:failure", e);

                        // Xử lý các loại lỗi cụ thể
                        if (e instanceof FirebaseNetworkException) {
                            Toast.makeText(getContext(), "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet của bạn.", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(getContext(), "Email không tồn tại hoặc đã bị vô hiệu hóa.", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getContext(), "Mật khẩu không đúng.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationEmailAgain(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Email xác thực đã được gửi lại. Vui lòng kiểm tra hộp thư của bạn.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể gửi lại email xác thực: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToRegister() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_dangnhap_to_dangky);
        }
    }

    private void forgotPassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email để đặt lại mật khẩu");
            etEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Email đặt lại mật khẩu đã được gửi", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể gửi email đặt lại mật khẩu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordVisible = false;
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordVisible = true;
        }
        // Move cursor to the end of text
        etPassword.setSelection(etPassword.getText().length());
    }

    private void navigateToHome() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_dangnhap_to_trangchu);
        }
    }
}
