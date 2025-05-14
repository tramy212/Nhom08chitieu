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
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.o7planning.nhom8_quanlychitieu.R;

public class NotificationSettingsFragment extends Fragment {

    private SwitchCompat switchTransactionNotifications;
    private SwitchCompat switchBudgetNotifications;
    private SwitchCompat switchGoalNotifications;
    private SwitchCompat switchWeeklyReports;
    private SwitchCompat switchMonthlyReports;
    private Button btnSaveNotificationSettings;
    private ImageView backButton;
    private Button btnBackToSettings;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        switchTransactionNotifications = root.findViewById(R.id.switchTransactionNotifications);
        switchBudgetNotifications = root.findViewById(R.id.switchBudgetNotifications);
        switchGoalNotifications = root.findViewById(R.id.switchGoalNotifications);
        switchWeeklyReports = root.findViewById(R.id.switchWeeklyReports);
        switchMonthlyReports = root.findViewById(R.id.switchMonthlyReports);
        btnSaveNotificationSettings = root.findViewById(R.id.btnSaveNotificationSettings);
        backButton = root.findViewById(R.id.backButton);
        btnBackToSettings = root.findViewById(R.id.btnBackToSettings);

        // Load current settings
        loadNotificationSettings();

        // Set click listeners
        btnSaveNotificationSettings.setOnClickListener(v -> {
            saveNotificationSettings();
        });

        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        btnBackToSettings.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return root;
    }

    private void loadNotificationSettings() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Here you would load the user's notification settings from Firebase
            // For now, we'll use default values
            switchTransactionNotifications.setChecked(true);
            switchBudgetNotifications.setChecked(true);
            switchGoalNotifications.setChecked(true);
            switchWeeklyReports.setChecked(false);
            switchMonthlyReports.setChecked(true);
        }
    }

    private void saveNotificationSettings() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the current settings
            boolean transactionNotifications = switchTransactionNotifications.isChecked();
            boolean budgetNotifications = switchBudgetNotifications.isChecked();
            boolean goalNotifications = switchGoalNotifications.isChecked();
            boolean weeklyReports = switchWeeklyReports.isChecked();
            boolean monthlyReports = switchMonthlyReports.isChecked();

            // Save to Firebase
            DatabaseReference userSettingsRef = mDatabase.child("UserSettings").child(currentUser.getUid()).child("notifications");
            userSettingsRef.child("transactionNotifications").setValue(transactionNotifications);
            userSettingsRef.child("budgetNotifications").setValue(budgetNotifications);
            userSettingsRef.child("goalNotifications").setValue(goalNotifications);
            userSettingsRef.child("weeklyReports").setValue(weeklyReports);
            userSettingsRef.child("monthlyReports").setValue(monthlyReports)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Cài đặt thông báo đã được lưu", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi lưu cài đặt: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
