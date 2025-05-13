package org.o7planning.nhom8_quanlychitieu.ui.DangKy;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.o7planning.nhom8_quanlychitieu.R;
import org.o7planning.nhom8_quanlychitieu.models.User;

public class DangKyFragment extends Fragment {

    private static final String TAG = "DangKyFragment";
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private ImageView ivTogglePassword, ivToggleConfirmPassword, backBtn;
    private Button btnRegister;
    private TextView tvLoginPrompt;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dang_ky, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        etName = root.findViewById(R.id.etName);
        etEmail = root.findViewById(R.id.etEmail);
        etPassword = root.findViewById(R.id.etPassword);
        etConfirmPassword = root.findViewById(R.id.etConfirmPassword);
        ivTogglePassword = root.findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = root.findViewById(R.id.ivToggleConfirmPassword);
        btnRegister = root.findViewById(R.id.btnRegister);
        tvLoginPrompt = root.findViewById(R.id.tvLoginPrompt);
        progressBar = root.findViewById(R.id.progressBar);
        backBtn = root.findViewById(R.id.backBtn);

        // Set click listeners
        btnRegister.setOnClickListener(v -> registerUser());
        tvLoginPrompt.setOnClickListener(v -> navigateToLogin());
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility(etPassword, ivTogglePassword, passwordVisible));
        ivToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivToggleConfirmPassword, confirmPasswordVisible));
        backBtn.setOnClickListener(v -> navigateToLogin());

        return root;
    }

    private void registerUser() {
        // Get input values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập họ và tên");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Log để debug
        Log.d(TAG, "Bắt đầu đăng ký với email: " + email);

        // Đăng ký người dùng với email và mật khẩu
        registerWithEmailVerification(name, email, password);
    }

    private void registerWithEmailVerification(String name, String email, String password) {
        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Gửi email xác thực
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailTask) {
                                                if (emailTask.isSuccessful()) {
                                                    Log.d(TAG, "Email verification sent.");
                                                    // Lưu thông tin người dùng vào database
                                                    User userModel = new User(user.getUid(), name, email);
                                                    mDatabase.child("Users").child(user.getUid()).setValue(userModel)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", Toast.LENGTH_LONG).show();
                                                                        // Đăng xuất người dùng sau khi gửi email xác thực
                                                                        mAuth.signOut();
                                                                        // Chuyển về màn hình đăng nhập
                                                                        navigateToLogin();
                                                                    } else {
                                                                        Log.e(TAG, "Failed to save user data", task.getException());
                                                                        Toast.makeText(getContext(), "Đăng ký thành công nhưng không thể lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                                                                        mAuth.signOut();
                                                                        navigateToLogin();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.e(TAG, "sendEmailVerification", emailTask.getException());
                                                    Toast.makeText(getContext(), "Đăng ký thành công nhưng không thể gửi email xác thực. Vui lòng liên hệ hỗ trợ.", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                    navigateToLogin();
                                                }
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Đăng ký thất bại: Không thể tạo người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.GONE);

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "Email đã được sử dụng bởi tài khoản khác", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Đăng ký thất bại", e);

                        // Xử lý các loại lỗi cụ thể
                        if (e instanceof FirebaseNetworkException) {
                            Toast.makeText(getContext(), "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet của bạn.", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(getContext(), "Mật khẩu quá yếu. Vui lòng chọn mật khẩu mạnh hơn.", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getContext(), "Email không hợp lệ.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email verification sent.");
                            Toast.makeText(getContext(), "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", Toast.LENGTH_LONG).show();
                            // Đăng xuất người dùng sau khi gửi email xác thực
                            mAuth.signOut();
                            // Chuyển về màn hình đăng nhập
                            navigateToLogin();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getContext(), "Đăng ký thành công nhưng không thể gửi email xác thực. Vui lòng liên hệ hỗ trợ.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            navigateToLogin();
                        }
                    }
                });
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isVisible) {
        if (isVisible) {
            // Hide password
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if (editText == etPassword) {
                passwordVisible = false;
            } else {
                confirmPasswordVisible = false;
            }
        } else {
            // Show password
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            if (editText == etPassword) {
                passwordVisible = true;
            } else {
                confirmPasswordVisible = true;
            }
        }
        // Move cursor to the end of text
        editText.setSelection(editText.getText().length());
    }

    private void navigateToLogin() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_dangky_to_dangnhap);
        }
    }
}
