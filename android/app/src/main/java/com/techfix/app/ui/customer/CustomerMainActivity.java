package com.techfix.app.ui.customer;

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
 * Customer main dashboard - placeholder for now.
 * Will be fully implemented in the feature/customer-ui branch.
 */
public class CustomerMainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        sessionManager = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole = findViewById(R.id.tvRole);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + sessionManager.getFullName() + "!");
        tvRole.setText("Role: " + sessionManager.getRole() + " | " + sessionManager.getEmail());

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
