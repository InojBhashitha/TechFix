package com.techfix.app.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.ui.auth.LoginActivity;
import com.techfix.app.utils.SessionManager;

/**
 * Staff management dashboard - placeholder for now.
 * Will be fully implemented in the feature/staff-management branch.
 */
public class StaffMainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);

        sessionManager = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole = findViewById(R.id.tvRole);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + sessionManager.getFullName() + "!");
        tvRole.setText("Staff Dashboard | " + sessionManager.getEmail());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            ApiClient.setAuthToken(null);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
