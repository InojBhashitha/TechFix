package com.techfix.app.ui.branches;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.BranchDto;
import com.techfix.app.data.remote.dto.BranchRecommendationRequestDto;
import com.techfix.app.data.remote.dto.BranchRecommendationResponseDto;
import com.techfix.app.ui.booking.BookRepairActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchesMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted = false;

    // UI elements
    private ProgressBar progressBar;
    private MaterialCardView cardGuide;
    private TextView tvGuideText;
    private MaterialCardView cardBranchDetail;
    private TextView tvBranchName;
    private TextView tvBranchDistance;
    private TextView tvBranchAddress;
    private TextView tvBranchHours;
    private TextView tagTechnicians;
    private TextView tagParts;
    private MaterialButton btnCallBranch;
    private MaterialButton btnBookHere;

    // State data
    private BranchRecommendationResponseDto mRecommendationData;
    private final Map<Marker, BranchRecommendationResponseDto.BranchDetailDto> mMarkerBranchMap = new HashMap<>();
    private BranchDto mSelectedBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupListeners();

        // Load map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        cardGuide = findViewById(R.id.cardGuide);
        tvGuideText = findViewById(R.id.tvGuideText);
        cardBranchDetail = findViewById(R.id.cardBranchDetail);
        tvBranchName = findViewById(R.id.tvBranchName);
        tvBranchDistance = findViewById(R.id.tvBranchDistance);
        tvBranchAddress = findViewById(R.id.tvBranchAddress);
        tvBranchHours = findViewById(R.id.tvBranchHours);
        tagTechnicians = findViewById(R.id.tagTechnicians);
        tagParts = findViewById(R.id.tagParts);
        btnCallBranch = findViewById(R.id.btnCallBranch);
        btnBookHere = findViewById(R.id.btnBookHere);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnCallBranch.setOnClickListener(v -> {
            if (mSelectedBranch != null && mSelectedBranch.getPhone() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSelectedBranch.getPhone()));
                startActivity(intent);
            }
        });

        btnBookHere.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookRepairActivity.class);
            if (mSelectedBranch != null) {
                intent.putExtra("branchId", mSelectedBranch.getId());
            }
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Customize map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMarkerClickListener(marker -> {
            BranchRecommendationResponseDto.BranchDetailDto detail = mMarkerBranchMap.get(marker);
            if (detail != null) {
                showBranchDetails(detail);
                marker.showInfoWindow();
                return true;
            }
            return false;
        });

        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
            } else {
                // If permission is denied, use Colombo as fallback
                Toast.makeText(this, "Location permission denied. Showing default Colombo view.", Toast.LENGTH_LONG).show();
                updateLocationUI();
                loadRecommendationWithFallback(6.9271, 79.8612); // Colombo
            }
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                progressBar.setVisibility(View.VISIBLE);
                tvGuideText.setText("Querying current location...");

                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    progressBar.setVisibility(View.GONE);
                    if (location != null) {
                        loadRecommendation(location.getLatitude(), location.getLongitude());
                    } else {
                        // Fallback if location returns null (common on emulators)
                        tvGuideText.setText("Location unavailable, using default Colombo coordinates");
                        loadRecommendationWithFallback(6.9271, 79.8612);
                    }
                });
            }
        } catch (SecurityException e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void loadRecommendation(double lat, double lon) {
        progressBar.setVisibility(View.VISIBLE);
        ApiService api = ApiClient.getApiService();
        BranchRecommendationRequestDto req = new BranchRecommendationRequestDto(lat, lon, null, null, null);

        api.recommendBranch(req).enqueue(new Callback<ApiResponse<BranchRecommendationResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<BranchRecommendationResponseDto>> call,
                                   @NonNull Response<ApiResponse<BranchRecommendationResponseDto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mRecommendationData = response.body().getData();
                    plotMapPoints();
                } else {
                    Toast.makeText(BranchesMapActivity.this, "Failed to retrieve recommended branch routing.", Toast.LENGTH_LONG).show();
                    plotFallbackMarkersWithoutRecommendation();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<BranchRecommendationResponseDto>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BranchesMapActivity.this, "Network error loading recommendations.", Toast.LENGTH_LONG).show();
                plotFallbackMarkersWithoutRecommendation();
            }
        });
    }

    private void loadRecommendationWithFallback(double lat, double lon) {
        cardGuide.setVisibility(View.VISIBLE);
        tvGuideText.setText("Viewing fallback: Colombo");
        loadRecommendation(lat, lon);
    }

    private void plotMapPoints() {
        if (mMap == null || mRecommendationData == null) return;

        mMap.clear();
        mMarkerBranchMap.clear();

        // 1. Add markers for each branch
        for (BranchRecommendationResponseDto.BranchDetailDto detail : mRecommendationData.getAllBranches()) {
            BranchDto branch = detail.getBranch();
            LatLng position = new LatLng(branch.getLatitude(), branch.getLongitude());

            MarkerOptions options = new MarkerOptions()
                    .position(position)
                    .title(branch.getName())
                    .snippet(String.format(Locale.getDefault(), "Distance: %.1f km", detail.getDistanceKm()));

            // Highlight recommended branch differently
            if (mRecommendationData.getRecommendedBranch().getId().equals(branch.getId())) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            } else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            Marker marker = mMap.addMarker(options);
            if (marker != null) {
                mMarkerBranchMap.put(marker, detail);
            }
        }

        // 2. Select and zoom to the recommended branch
        BranchDto recommended = mRecommendationData.getRecommendedBranch();
        LatLng zoomTarget = new LatLng(recommended.getLatitude(), recommended.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomTarget, 10f));

        // Find the detail object matching the recommended branch
        for (BranchRecommendationResponseDto.BranchDetailDto detail : mRecommendationData.getAllBranches()) {
            if (detail.getBranch().getId().equals(recommended.getId())) {
                showBranchDetails(detail);
                tvGuideText.setText("Best branch: " + recommended.getName());
                break;
            }
        }
    }

    private void showBranchDetails(BranchRecommendationResponseDto.BranchDetailDto detail) {
        cardBranchDetail.setVisibility(View.VISIBLE);
        mSelectedBranch = detail.getBranch();

        tvBranchName.setText(mSelectedBranch.getName());
        tvBranchDistance.setText(String.format(Locale.getDefault(), "%.1f km", detail.getDistanceKm()));
        tvBranchAddress.setText(mSelectedBranch.getAddress());
        tvBranchHours.setText("Hours: " + detail.getOpeningHours());

        // Technicians Tag
        if (detail.getIsTechnicianAvailable()) {
            tagTechnicians.setText("Technician Available");
            tagTechnicians.setTextColor(ContextCompat.getColor(this, R.color.status_completed));
            tagTechnicians.setBackgroundResource(R.drawable.bg_status_badge);
            tagTechnicians.getBackground().setTint(ContextCompat.getColor(this, R.color.status_completed_bg));
        } else {
            tagTechnicians.setText("Technicians Busy");
            tagTechnicians.setTextColor(ContextCompat.getColor(this, R.color.status_in_progress));
            tagTechnicians.setBackgroundResource(R.drawable.bg_status_badge);
            tagTechnicians.getBackground().setTint(ContextCompat.getColor(this, R.color.status_in_progress_bg));
        }

        // Parts Tag
        if (detail.getIsPartAvailable()) {
            tagParts.setText("Spare Parts Available");
            tagParts.setTextColor(ContextCompat.getColor(this, R.color.status_completed));
            tagParts.setBackgroundResource(R.drawable.bg_status_badge);
            tagParts.getBackground().setTint(ContextCompat.getColor(this, R.color.status_completed_bg));
        } else {
            tagParts.setText("Parts Out of Stock");
            tagParts.setTextColor(ContextCompat.getColor(this, R.color.status_cancelled));
            tagParts.setBackgroundResource(R.drawable.bg_status_badge);
            tagParts.getBackground().setTint(ContextCompat.getColor(this, R.color.status_cancelled_bg));
        }
    }

    private void plotFallbackMarkersWithoutRecommendation() {
        if (mMap == null) return;
        mMap.clear();

        // Mock details for Colombo and Galle
        LatLng colomboPos = new LatLng(6.9271, 79.8612);
        LatLng gallePos = new LatLng(6.0535, 80.2210);

        mMap.addMarker(new MarkerOptions()
                .position(colomboPos)
                .title("TechFix Colombo Service Center")
                .snippet("+94 11 234 5678")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.addMarker(new MarkerOptions()
                .position(gallePos)
                .title("TechFix Galle Service Center")
                .snippet("+94 91 223 4567")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colomboPos, 8f));
    }
}
