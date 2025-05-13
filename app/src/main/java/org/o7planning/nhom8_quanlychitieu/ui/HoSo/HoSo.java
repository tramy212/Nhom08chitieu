package org.o7planning.nhom8_quanlychitieu.ui.HoSo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.o7planning.nhom8_quanlychitieu.R;

public class HoSo extends Fragment {

    private TextView userName, userEmail;
    private LinearLayout notificationSettings, passwordSettings;
    private LinearLayout logoutBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Sửa lỗi: R.layout_fragment_ho_so -> R.layout.fragment_ho_so
        View root = inflater.inflate(R.layout.fragment_ho_so, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        userName = root.findViewById(R.id.userName);
        userEmail = root.findViewById(R.id.userEmail);
        notificationSettings = root.findViewById(R.id.notificationSettings);
        passwordSettings = root.findViewById(R.id.passwordSettings);
        logoutBtn = root.findViewById(R.id.logoutBtn);

        // Load user data
        loadUserData();

        // Set click listeners
        notificationSettings.setOnClickListener(v -> {
            // Kiểm tra xem action có tồn tại không trước khi navigate
            try {
                Navigation.findNavController(v).navigate(R.id.action_hoso_to_notificationSettings);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        passwordSettings.setOnClickListener(v -> {
            // Kiểm tra xem action có tồn tại không trước khi navigate
            try {
                Navigation.findNavController(v).navigate(R.id.action_hoso_to_passwordSettings);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            // Navigate to login screen or main activity
            // Navigation.findNavController(v).navigate(R.id.action_hoso_to_login);
        });

        return root;
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
                                    String name = snapshot.child("name").getValue(String.class);
                                    if (name != null) {
                                        userName.setText(name);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}