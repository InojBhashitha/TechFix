package com.techfix.app.ui.tracking;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.RepairTrackingDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepairTrackingActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private NestedScrollView layoutContent;
    private FrameLayout layoutLoading;
    private LinearLayout layoutError;
    private TextView tvErrorMessage;
    private MaterialButton btnRetry;

    private TextView tvBookingRef;
    private TextView tvDeviceName;
    private TextView tvProblemDesc;
    private TextView tvCurrentStatus;

    private RecyclerView rvTimeline;
    private RepairTrackingAdapter trackingAdapter;

    private String bookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_tracking);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        bookingRef = getIntent().getStringExtra("bookingRef");
        if (bookingRef == null || bookingRef.trim().isEmpty()) {
            showError("No booking reference was provided.");
        } else {
            loadTrackingData();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        layoutContent = findViewById(R.id.layoutContent);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutError = findViewById(R.id.layoutError);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetry = findViewById(R.id.btnRetry);

        tvBookingRef = findViewById(R.id.tvBookingRef);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvProblemDesc = findViewById(R.id.tvProblemDesc);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);

        rvTimeline = findViewById(R.id.rvTimeline);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        trackingAdapter = new RepairTrackingAdapter(this);
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rvTimeline.setAdapter(trackingAdapter);
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> {
            if (bookingRef != null && !bookingRef.trim().isEmpty()) {
                loadTrackingData();
            } else {
                showError("No booking reference was provided.");
            }
        });
    }

    private void loadTrackingData() {
        showLoading();
        ApiService api = ApiClient.getApiService();

        api.getBookingTracking(bookingRef).enqueue(new Callback<ApiResponse<RepairTrackingDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<RepairTrackingDto>> call, @NonNull Response<ApiResponse<RepairTrackingDto>> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    RepairTrackingDto dto = response.body().getData();
                    if (dto != null) {
                        bindData(dto);
                        showContent();
                    } else {
                        showError("Empty tracking details received.");
                    }
                } else {
                    String errorMsg = "Booking details not found or access restricted.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.message();
                        } catch (Exception ignored) {}
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<RepairTrackingDto>> call, @NonNull Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                showError("Network connection failure. Please verify connection and retry.");
            }
        });
    }

    private void bindData(RepairTrackingDto dto) {
        tvBookingRef.setText(dto.getBookingReference());
        tvDeviceName.setText(dto.getDeviceBrand() + " " + dto.getDeviceModel());
        tvProblemDesc.setText("Problem: " + (dto.getProblemDescription() != null ? dto.getProblemDescription() : "N/A"));
        
        bindStatusBadge(dto.getCurrentStatus(), dto.getCurrentStatusDisplayName());

        trackingAdapter.setTrackingData(dto);
    }

    private void bindStatusBadge(String status, String displayName) {
        tvCurrentStatus.setText(displayName != null ? displayName : status);
        if (status == null) return;

        int textColorRes;
        int bgColorRes;

        switch (status) {
            case "REQUEST_SUBMITTED":
                textColorRes = R.color.status_submitted;
                bgColorRes = R.color.status_submitted_bg;
                break;
            case "COMPLETED":
                textColorRes = R.color.status_completed;
                bgColorRes = R.color.status_completed_bg;
                break;
            case "READY_FOR_COLLECTION":
                textColorRes = R.color.status_ready;
                bgColorRes = R.color.status_ready_bg;
                break;
            case "CANCELLED":
                textColorRes = R.color.status_cancelled;
                bgColorRes = R.color.status_cancelled_bg;
                break;
            default: // In progress states
                textColorRes = R.color.status_in_progress;
                bgColorRes = R.color.status_in_progress_bg;
                break;
        }

        tvCurrentStatus.setTextColor(ContextCompat.getColor(this, textColorRes));
        tvCurrentStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, bgColorRes)));
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutLoading.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        layoutLoading.setVisibility(View.GONE);
        layoutContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }
}
