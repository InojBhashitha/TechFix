package com.techfix.app.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.BookingResponseDto;
import com.techfix.app.data.remote.dto.StaffDashboardStatsDto;
import com.techfix.app.ui.adapters.StaffRepairQueueAdapter;
import com.techfix.app.ui.auth.LoginActivity;
import com.techfix.app.ui.tracking.RepairTrackingActivity;
import com.techfix.app.utils.SessionManager;

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
                // Handled gracefully in loadRepairQueue
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
        Toast.makeText(this, "Update status for " + booking.getBookingReference(), Toast.LENGTH_SHORT).show();
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
}
