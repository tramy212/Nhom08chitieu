package org.o7planning.nhom8_quanlychitieu.ui.HoSo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import org.o7planning.nhom8_quanlychitieu.R;

public class LogoutConfirmationFragment extends Fragment {

    private static final String TAG = "LogoutConfirmation";
    private Button btnCancel, btnConfirmLogout;
    private ImageView backButton;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_logout_confirmation, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        btnCancel = root.findViewById(R.id.btnCancel);
        btnConfirmLogout = root.findViewById(R.id.btnConfirmLogout);
        backButton = root.findViewById(R.id.backButton);

        // Set click listeners
        btnCancel.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        btnConfirmLogout.setOnClickListener(v -> {
            logout();
        });

        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return root;
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_logoutConfirmation_to_dangnhap);
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: ", e);
            // If direct navigation fails, try another approach
            try {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.dangnhap);
            } catch (Exception ex) {
                Log.e(TAG, "Second navigation error: ", ex);
                Toast.makeText(getContext(), "Không thể chuyển về màn hình đăng nhập. Vui lòng khởi động lại ứng dụng.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
