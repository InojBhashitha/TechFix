package com.techfix.app.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.BookingResponseDto;
import com.techfix.app.data.remote.dto.StaffDashboardStatsDto;
import com.techfix.app.data.remote.dto.UpdateRepairStatusRequestDto;
import com.techfix.app.ui.adapters.StaffRepairQueueAdapter;
import com.techfix.app.ui.auth.LoginActivity;
import com.techfix.app.ui.tracking.RepairTrackingActivity;
import com.techfix.app.utils.SessionManager;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffMainActivity extends AppCompatActivity implements StaffRepairQueueAdapter.OnStaffRepairActionListener {

    private SessionManager sessionManager;
    private ApiService apiService;

    private TextView tvWelcome, tvRole;
    private TextView tvCountPending, tvCountInProgress, tvCountCompleted, tvCountTotal;
    private TextView tvLowStockAlerts, tvAvailableTechs;
    private ImageButton btnRefresh, btnInventory, btnLogout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvRepairQueue;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private ChipGroup chipGroupFilter;

    private StaffRepairQueueAdapter queueAdapter;
    private String currentStatusFilter = null;

    private static final String[] STATUS_DISPLAY_NAMES = {
            "Request Submitted",
            "Branch Assigned",
            "Device Received",
            "Diagnostic & Inspection",
            "Repair in Progress",
            "Quality Check (QA)",
            "Ready for Collection",
            "Repair Completed",
            "Cancelled"
    };

    private static final String[] STATUS_ENUM_KEYS = {
            "REQUEST_SUBMITTED",
            "BRANCH_ASSIGNED",
            "DEVICE_RECEIVED",
            "DIAGNOSIS",
            "REPAIRING",
            "QUALITY_CHECK",
            "READY_FOR_COLLECTION",
            "COMPLETED",
            "CANCELLED"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();

        initViews();
        setupHeader();
        setupListeners();
        refreshAllData();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvRole = findViewById(R.id.tvRole);

        tvCountPending = findViewById(R.id.tvCountPending);
        tvCountInProgress = findViewById(R.id.tvCountInProgress);
        tvCountCompleted = findViewById(R.id.tvCountCompleted);
        tvCountTotal = findViewById(R.id.tvCountTotal);

        tvLowStockAlerts = findViewById(R.id.tvLowStockAlerts);
        tvAvailableTechs = findViewById(R.id.tvAvailableTechs);

        btnRefresh = findViewById(R.id.btnRefresh);
        btnInventory = findViewById(R.id.btnInventory);
        btnLogout = findViewById(R.id.btnLogout);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rvRepairQueue = findViewById(R.id.rvRepairQueue);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        progressBar = findViewById(R.id.progressBar);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);

        rvRepairQueue.setLayoutManager(new LinearLayoutManager(this));
        queueAdapter = new StaffRepairQueueAdapter(this);
        rvRepairQueue.setAdapter(queueAdapter);
    }

    private void setupHeader() {
        tvWelcome.setText("Welcome, " + sessionManager.getFullName() + "!");
        tvRole.setText("Staff Dashboard | " + sessionManager.getEmail());
    }

    private void setupListeners() {
        btnRefresh.setOnClickListener(v -> refreshAllData());

        swipeRefreshLayout.setOnRefreshListener(this::refreshAllData);

        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentStatusFilter = null;
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipPending) {
                    currentStatusFilter = "REQUEST_SUBMITTED";
                } else if (checkedId == R.id.chipInProgress) {
                    currentStatusFilter = "REPAIRING";
                } else {
                    currentStatusFilter = null;
                }
            }
            loadRepairQueue();
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            ApiClient.setAuthToken(null);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void refreshAllData() {
        loadDashboardStats();
        loadRepairQueue();
    }

    private void loadDashboardStats() {
        apiService.getStaffDashboardStats(null).enqueue(new Callback<ApiResponse<StaffDashboardStatsDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<StaffDashboardStatsDto>> call, Response<ApiResponse<StaffDashboardStatsDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StaffDashboardStatsDto stats = response.body().getData();
                    updateStatsUI(stats);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<StaffDashboardStatsDto>> call, Throwable t) {
                // Handled gracefully
            }
        });
    }

    private void loadRepairQueue() {
        swipeRefreshLayout.setRefreshing(true);

        apiService.getStaffBookings(null, currentStatusFilter).enqueue(new Callback<ApiResponse<List<BookingResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BookingResponseDto>>> call, Response<ApiResponse<List<BookingResponseDto>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BookingResponseDto> bookings = response.body().getData();
                    if (bookings != null && !bookings.isEmpty()) {
                        layoutEmpty.setVisibility(View.GONE);
                        rvRepairQueue.setVisibility(View.VISIBLE);
                        queueAdapter.setBookings(bookings);
                    } else {
                        rvRepairQueue.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        queueAdapter.setBookings(null);
                    }
                } else {
                    Toast.makeText(StaffMainActivity.this, "Failed to fetch repair queue", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BookingResponseDto>>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(StaffMainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatsUI(StaffDashboardStatsDto stats) {
        if (stats == null) return;

        tvCountPending.setText(String.valueOf(stats.getPendingRepairs() != null ? stats.getPendingRepairs() : 0));
        tvCountInProgress.setText(String.valueOf(stats.getInProgressRepairs() != null ? stats.getInProgressRepairs() : 0));
        tvCountCompleted.setText(String.valueOf(stats.getCompletedRepairs() != null ? stats.getCompletedRepairs() : 0));
        tvCountTotal.setText(String.valueOf(stats.getTotalRepairs() != null ? stats.getTotalRepairs() : 0));

        tvLowStockAlerts.setText("Low Stock: " + (stats.getLowStockAlerts() != null ? stats.getLowStockAlerts() : 0));
        tvAvailableTechs.setText("Available Techs: " + (stats.getAvailableTechnicians() != null ? stats.getAvailableTechnicians() : 0));
    }

    @Override
    public void onUpdateStatus(BookingResponseDto booking) {
        showUpdateStatusDialog(booking);
    }

    @Override
    public void onAssignTechnician(BookingResponseDto booking) {
        Toast.makeText(this, "Assign technician for " + booking.getBookingReference(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(BookingResponseDto booking) {
        if (booking.getBookingReference() != null) {
            Intent intent = new Intent(this, RepairTrackingActivity.class);
            intent.putExtra("BOOKING_REFERENCE", booking.getBookingReference());
            startActivity(intent);
        }
    }

    private void showUpdateStatusDialog(BookingResponseDto booking) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_status, null);

        TextView tvDialogBookingRef = dialogView.findViewById(R.id.tvDialogBookingRef);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        EditText etStatusNotes = dialogView.findViewById(R.id.etStatusNotes);
        EditText etAdditionalCost = dialogView.findViewById(R.id.etAdditionalCost);
        MaterialButton btnCancelStatus = dialogView.findViewById(R.id.btnCancelStatus);
        MaterialButton btnSubmitStatus = dialogView.findViewById(R.id.btnSubmitStatus);

        String currentStatusName = booking.getStatusDisplayName() != null ? booking.getStatusDisplayName() : booking.getCurrentStatus();
        tvDialogBookingRef.setText("Booking: " + booking.getBookingReference() + " • Current: " + currentStatusName);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATUS_DISPLAY_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Pre-select current status in spinner if possible
        if (booking.getCurrentStatus() != null) {
            for (int i = 0; i < STATUS_ENUM_KEYS.length; i++) {
                if (STATUS_ENUM_KEYS[i].equalsIgnoreCase(booking.getCurrentStatus())) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancelStatus.setOnClickListener(v -> dialog.dismiss());

        btnSubmitStatus.setOnClickListener(v -> {
            int selectedIndex = spinnerStatus.getSelectedItemPosition();
            if (selectedIndex < 0 || selectedIndex >= STATUS_ENUM_KEYS.length) {
                Toast.makeText(this, "Please select a valid status", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedStatusKey = STATUS_ENUM_KEYS[selectedIndex];
            String notes = etStatusNotes.getText().toString().trim();
            String costStr = etAdditionalCost.getText().toString().trim();

            BigDecimal additionalCost = null;
            if (!costStr.isEmpty()) {
                try {
                    additionalCost = new BigDecimal(costStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid additional cost format", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            btnSubmitStatus.setEnabled(false);
            btnSubmitStatus.setText("Updating...");

            UpdateRepairStatusRequestDto request = new UpdateRepairStatusRequestDto(selectedStatusKey, notes, additionalCost);

            apiService.updateRepairStatus(booking.getBookingReference(), request).enqueue(new Callback<ApiResponse<BookingResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<BookingResponseDto>> call, Response<ApiResponse<BookingResponseDto>> response) {
                    btnSubmitStatus.setEnabled(true);
                    btnSubmitStatus.setText("Update Status");

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(StaffMainActivity.this, "Status updated to " + response.body().getData().getStatusDisplayName(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        refreshAllData();
                    } else {
                        String errMsg = "Failed to update status";
                        if (response.body() != null && response.body().getMessage() != null) {
                            errMsg = response.body().getMessage();
                        }
                        Toast.makeText(StaffMainActivity.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<BookingResponseDto>> call, Throwable t) {
                    btnSubmitStatus.setEnabled(true);
                    btnSubmitStatus.setText("Update Status");
                    Toast.makeText(StaffMainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
