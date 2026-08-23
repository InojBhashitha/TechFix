package com.techfix.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.AuthResponse;
import com.techfix.app.data.remote.dto.LoginRequest;
import com.techfix.app.ui.admin.AdminMainActivity;
import com.techfix.app.ui.customer.CustomerMainActivity;
import com.techfix.app.ui.staff.StaffMainActivity;
import com.techfix.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvError, tvGoToRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        tvError.setVisibility(View.GONE);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.err_empty_field));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_field));
            return;
        }
        if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_password_short));
            return;
        }

        setLoading(true);

        ApiService api = ApiClient.getApiService();
        LoginRequest request = new LoginRequest(email, password);

        api.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse auth = response.body().getData();
                    handleAuthSuccess(auth);
                } else {
                    String errorMsg = "Invalid email or password";
                    if (response.body() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                setLoading(false);
                showError("Network error. Please check your connection.");
            }
        });
    }

    private void handleAuthSuccess(AuthResponse auth) {
        sessionManager.saveSession(
                auth.getToken(),
                auth.getUserId(),
                auth.getFullName(),
                auth.getEmail(),
                auth.getRole()
        );

        ApiClient.setAuthToken(auth.getToken());

        Toast.makeText(this, "Welcome back, " + auth.getFullName() + "!", Toast.LENGTH_SHORT).show();

        // Separate Routing per role
        Intent intent;
        if ("ADMIN".equalsIgnoreCase(auth.getRole())) {
            intent = new Intent(this, AdminMainActivity.class);
        } else if ("STAFF".equalsIgnoreCase(auth.getRole())) {
            intent = new Intent(this, StaffMainActivity.class);
        } else {
            intent = new Intent(this, CustomerMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
