package com.techfix.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.ui.admin.AdminMainActivity;
import com.techfix.app.ui.customer.CustomerMainActivity;
import com.techfix.app.ui.staff.StaffMainActivity;
import com.techfix.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);

            Intent intent;
            if (session.isLoggedIn()) {
                // Restore auth token for API calls
                ApiClient.setAuthToken(session.getToken());

                // Route by role
                if (session.isAdmin()) {
                    intent = new Intent(this, AdminMainActivity.class);
                } else if (session.isStaff()) {
                    intent = new Intent(this, StaffMainActivity.class);
                } else {
                    intent = new Intent(this, CustomerMainActivity.class);
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }
}
