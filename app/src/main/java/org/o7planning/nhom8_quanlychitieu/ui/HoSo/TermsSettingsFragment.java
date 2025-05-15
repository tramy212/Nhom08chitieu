package org.o7planning.nhom8_quanlychitieu.ui.HoSo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.o7planning.nhom8_quanlychitieu.R;

public class TermsSettingsFragment extends Fragment {

    private Button btnAcceptTerms;
    private ImageView backButton;
    // Đã xóa khai báo btnBackToSettings

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terms_settings, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        btnAcceptTerms = root.findViewById(R.id.btnAcceptTerms);
        backButton = root.findViewById(R.id.backButton);
        // Đã xóa dòng tham chiếu đến btnBackToSettings

        // Set click listeners
        btnAcceptTerms.setOnClickListener(v -> {
            acceptTerms();
        });

        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Đã xóa click listener cho btnBackToSettings

        return root;
    }

    private void acceptTerms() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Save acceptance to Firebase
            DatabaseReference userSettingsRef = mDatabase.child("UserSettings").child(currentUser.getUid());
            userSettingsRef.child("termsAccepted").setValue(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Bạn đã đồng ý với các điều khoản và điều kiện", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi lưu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}