package org.o7planning.nhom8_quanlychitieu;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.o7planning.nhom8_quanlychitieu.databinding.ActivityMainBinding;
import org.o7planning.nhom8_quanlychitieu.utils.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ẩn ActionBar mặc định
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Cập nhật AppBarConfiguration để bao gồm tất cả các ID fragment
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.trangchu, R.id.giaodich, R.id.themmoigiaodich, R.id.danhmuc, R.id.hoso)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // XÓA DÒNG NÀY
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        // Kiểm tra kết nối Firebase
        try {
            Log.d(TAG, "Bắt đầu kiểm tra kết nối Firebase...");
            FirebaseHelper.testConnection();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi kiểm tra kết nối Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}