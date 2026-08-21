package com.techfix.app.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.DeviceCategoryDto;
import com.techfix.app.data.remote.dto.RepairServiceDto;
import com.techfix.app.ui.adapters.CategoryAdapter;
import com.techfix.app.ui.adapters.ServiceAdapter;
import com.techfix.app.ui.booking.BookRepairActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private TextInputEditText etSearchServices;
    private RecyclerView rvCategories, rvServices;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private CategoryAdapter categoryAdapter;
    private ServiceAdapter serviceAdapter;

    private List<RepairServiceDto> allLoadedServices = new ArrayList<>();
    private Long selectedCategoryId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);

        initViews(root);
        setupRecyclerViews();
        setupListeners();

        loadCategories();
        loadServices(null);

        return root;
    }

    private void initViews(View root) {
        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        etSearchServices = root.findViewById(R.id.etSearchServices);
        rvCategories = root.findViewById(R.id.rvCategories);
        rvServices = root.findViewById(R.id.rvServices);
        progressBar = root.findViewById(R.id.progressBar);
        tvEmptyState = root.findViewById(R.id.tvEmptyState);
    }

    private void setupRecyclerViews() {
        // Categories horizontal list
        categoryAdapter = new CategoryAdapter(category -> {
            selectedCategoryId = (category != null) ? category.getId() : null;
            loadServices(selectedCategoryId);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Services vertical list
        serviceAdapter = new ServiceAdapter(service -> {
            Intent intent = new Intent(requireContext(), BookRepairActivity.class);
            intent.putExtra("serviceId", service.getId());
            if (service.getCategory() != null) {
                intent.putExtra("categoryId", service.getCategory().getId());
            }
            startActivity(intent);
        });
        rvServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvServices.setAdapter(serviceAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadCategories();
            loadServices(selectedCategoryId);
        });

        etSearchServices.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterServices(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCategories() {
        ApiService api = ApiClient.getApiService();
        api.getCategories().enqueue(new Callback<ApiResponse<List<DeviceCategoryDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DeviceCategoryDto>>> call, Response<ApiResponse<List<DeviceCategoryDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<DeviceCategoryDto> categories = response.body().getData();
                    categoryAdapter.setCategories(categories);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DeviceCategoryDto>>> call, Throwable t) {}
        });
    }

    private void loadServices(@Nullable Long categoryId) {
        showLoading(true);
        ApiService api = ApiClient.getApiService();

        api.getServices(categoryId).enqueue(new Callback<ApiResponse<List<RepairServiceDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RepairServiceDto>>> call, Response<ApiResponse<List<RepairServiceDto>>> response) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allLoadedServices = response.body().getData();
                    filterServices(etSearchServices.getText() != null ? etSearchServices.getText().toString() : "");
                } else {
                    showEmptyState("Failed to load repair services.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RepairServiceDto>>> call, Throwable t) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                showEmptyState("Network error. Could not load services.");
            }
        });
    }

    private void filterServices(String query) {
        String trimmed = query.trim().toLowerCase();
        List<RepairServiceDto> filtered = new ArrayList<>();

        for (RepairServiceDto service : allLoadedServices) {
            boolean matchesName = service.getName() != null && service.getName().toLowerCase().contains(trimmed);
            boolean matchesDesc = service.getDescription() != null && service.getDescription().toLowerCase().contains(trimmed);
            boolean matchesCategory = service.getCategory() != null && service.getCategory().getName().toLowerCase().contains(trimmed);

            if (trimmed.isEmpty() || matchesName || matchesDesc || matchesCategory) {
                filtered.add(service);
            }
        }

        serviceAdapter.setServices(filtered);

        if (filtered.isEmpty()) {
            showEmptyState("No repair services found matching '" + query + "'.");
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvServices.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String message) {
        tvEmptyState.setText(message);
        tvEmptyState.setVisibility(View.VISIBLE);
        rvServices.setVisibility(View.GONE);
    }
}
