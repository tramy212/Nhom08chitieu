package org.o7planning.nhom8_quanlychitieu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private NavController navController;
    private BottomNavigationView navView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Thiết lập navigation
        navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.trangchu, R.id.giaodich, R.id.themmoigiaodich, R.id.danhmuc, R.id.hoso)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // BỎ DÒNG NÀY ĐỂ TRÁNH LỖI
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navView, navController);

        // Ẩn bottom navigation khi ở màn hình đăng nhập
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.dangnhap || destination.getId() == R.id.dangky) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });

        // Kiểm tra trạng thái đăng nhập
        if (mAuth.getCurrentUser() == null) {
            navController.navigate(R.id.dangnhap);
        }

        // Thêm dữ liệu mẫu vào Firebase nếu chưa có
        addSampleUserIfNeeded();
    }

    private void addSampleUserIfNeeded() {
        // Tạo tài khoản mẫu trong Firebase Authentication
        String email = "johnsmith@example.com";
        String password = "123456";

        try {
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    try {
                        boolean userExists = !task.getResult().getSignInMethods().isEmpty();

                        if (!userExists) {
                            // Tạo tài khoản trong Firebase Authentication
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            // Lưu thông tin bổ sung vào Realtime Database
                                            FirebaseUser user = authTask.getResult().getUser();
                                            if (user != null) {
                                                mDatabase.child("Users").child(user.getUid()).child("email").setValue(email);
                                                mDatabase.child("Users").child(user.getUid()).child("name").setValue("John Smith");
                                                mDatabase.child("Users").child(user.getUid()).child("userId").setValue(user.getUid());

                                                Log.d(TAG, "Tài khoản mẫu đã được tạo thành công");

                                                // Đăng xuất sau khi tạo tài khoản mẫu
                                                mAuth.signOut();
                                            }
                                        } else {
                                            Log.e(TAG, "Không thể tạo tài khoản mẫu: " + authTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Tài khoản mẫu đã tồn tại");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi kiểm tra tài khoản mẫu: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Lỗi khi kiểm tra tài khoản mẫu: " + task.getException().getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi thiết lập tài khoản mẫu: " + e.getMessage());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
