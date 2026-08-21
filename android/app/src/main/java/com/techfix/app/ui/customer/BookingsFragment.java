package com.techfix.app.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.BookingResponseDto;
import com.techfix.app.ui.adapters.BookingAdapter;
import com.techfix.app.ui.booking.BookRepairActivity;
import com.techfix.app.ui.tracking.RepairTrackingActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvBookings;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnBookFirstRepair;
    private BookingAdapter bookingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings, container, false);

        initViews(root);
        setupRecyclerView();
        setupListeners();

        loadBookings();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    private void initViews(View root) {
        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        rvBookings = root.findViewById(R.id.rvBookings);
        progressBar = root.findViewById(R.id.progressBar);
        layoutEmptyState = root.findViewById(R.id.layoutEmptyState);
        btnBookFirstRepair = root.findViewById(R.id.btnBookFirstRepair);
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(booking -> {
            Intent intent = new Intent(requireContext(), RepairTrackingActivity.class);
            intent.putExtra("bookingRef", booking.getBookingReference());
            startActivity(intent);
        });
        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBookings.setAdapter(bookingAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadBookings);

        btnBookFirstRepair.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BookRepairActivity.class);
            startActivity(intent);
        });
    }

    private void loadBookings() {
        showLoading(true);
        ApiService api = ApiClient.getApiService();

        api.getMyBookings().enqueue(new Callback<ApiResponse<List<BookingResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BookingResponseDto>>> call, Response<ApiResponse<List<BookingResponseDto>>> response) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BookingResponseDto> bookings = response.body().getData();
                    bookingAdapter.setBookings(bookings);

                    if (bookings == null || bookings.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }
                } else {
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BookingResponseDto>>> call, Throwable t) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                showEmptyState(true);
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(boolean empty) {
        layoutEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvBookings.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
