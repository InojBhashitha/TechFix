package com.techfix.app.ui.booking;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.ApiService;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.BookingRequestDto;
import com.techfix.app.data.remote.dto.BookingResponseDto;
import com.techfix.app.data.remote.dto.BranchRecommendationRequestDto;
import com.techfix.app.data.remote.dto.BranchRecommendationResponseDto;
import com.techfix.app.data.remote.dto.DeviceCategoryDto;
import com.techfix.app.data.remote.dto.RepairServiceDto;
import com.techfix.app.ui.adapters.RepairImageAdapter;
import com.techfix.app.ui.adapters.SelectedImage;
import com.bumptech.glide.Glide;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRepairActivity extends AppCompatActivity {

    private int currentStep = 1;

    // Step Layouts & Indicators
    private LinearLayout layoutStep1, layoutStep2, layoutStep3, layoutStep4;
    private TextView step1Indicator, step2Indicator, step3Indicator, step4Indicator;

    // Step 1 Controls
    private Spinner spinnerCategory, spinnerService;
    private MaterialCardView cardSelectedServiceInfo;
    private TextView tvSelServiceName, tvSelServiceDesc, tvSelServicePrice;

    // Step 2 Controls
    private TextInputEditText etDeviceBrand, etDeviceModel, etSerialNumber, etProblemDescription;

    // Step 3 Controls
    private RadioGroup rgBranch;
    private RadioButton rbColombo, rbGalle;
    private MaterialButton btnPickDate, btnPickTime;
    private TextView tvSelectedDateTime;

    // Step 4 Controls
    private TextView tvSumService, tvSumDevice, tvSumBranch, tvSumAppt, tvSumPrice;

    // Bottom Controls & Status
    private MaterialButton btnBack, btnNext;
    private ProgressBar progressBar;
    private TextView tvError;

    // Data lists & selections
    private List<DeviceCategoryDto> categories = new ArrayList<>();
    private List<RepairServiceDto> services = new ArrayList<>();

    private DeviceCategoryDto selectedCategory;
    private RepairServiceDto selectedService;
    private Long preSelectedServiceId = null;

    private Calendar appointmentCalendar = Calendar.getInstance();

    // GPS Smart Allocation fields
    private FusedLocationProviderClient fusedLocationClient;
    private Double customerLatitude = null;
    private Double customerLongitude = null;
    private MaterialCardView cardRecommendation;
    private TextView tvRecBranchName, tvRecDistance, tvRecTechnician, tvRecParts, tvRecReason;
    private MaterialButton btnConfirmBranch;
    private boolean isBranchConfirmed = false;

    // Repair Image fields
    private MaterialButton btnTakePhoto, btnChooseGallery;
    private RecyclerView rvRepairImages;
    private TextView tvNoImages;
    private RepairImageAdapter imageAdapter;
    private final List<SelectedImage> selectedImages = new ArrayList<>();
    private Uri tempCameraUri;
    private String createdBookingReference = null;
    private android.app.ProgressDialog uploadProgressDialog = null;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied. Cannot take photo.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            isSuccess -> {
                if (isSuccess && tempCameraUri != null) {
                    selectedImages.add(new SelectedImage(tempCameraUri));
                    imageAdapter.setImageList(selectedImages);
                    updateImagesStateUI();
                } else {
                    Toast.makeText(this, "Camera capture cancelled or failed.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    for (Uri uri : uris) {
                        boolean exists = false;
                        for (SelectedImage img : selectedImages) {
                            if (img.getUri().equals(uri)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            selectedImages.add(new SelectedImage(uri));
                        }
                    }
                    imageAdapter.setImageList(selectedImages);
                    updateImagesStateUI();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_repair);

        if (getIntent().hasExtra("serviceId")) {
            preSelectedServiceId = getIntent().getLongExtra("serviceId", -1);
        }

        initViews();
        setupListeners();
        updateStepUI();

        // Load Categories from API
        loadCategories();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);
        layoutStep3 = findViewById(R.id.layoutStep3);
        layoutStep4 = findViewById(R.id.layoutStep4);

        step1Indicator = findViewById(R.id.step1Indicator);
        step2Indicator = findViewById(R.id.step2Indicator);
        step3Indicator = findViewById(R.id.step3Indicator);
        step4Indicator = findViewById(R.id.step4Indicator);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerService = findViewById(R.id.spinnerService);
        cardSelectedServiceInfo = findViewById(R.id.cardSelectedServiceInfo);
        tvSelServiceName = findViewById(R.id.tvSelServiceName);
        tvSelServiceDesc = findViewById(R.id.tvSelServiceDesc);
        tvSelServicePrice = findViewById(R.id.tvSelServicePrice);

        etDeviceBrand = findViewById(R.id.etDeviceBrand);
        etDeviceModel = findViewById(R.id.etDeviceModel);
        etSerialNumber = findViewById(R.id.etSerialNumber);
        etProblemDescription = findViewById(R.id.etProblemDescription);

        rgBranch = findViewById(R.id.rgBranch);
        rbColombo = findViewById(R.id.rbColombo);
        rbGalle = findViewById(R.id.rbGalle);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);

        tvSumService = findViewById(R.id.tvSumService);
        tvSumDevice = findViewById(R.id.tvSumDevice);
        tvSumBranch = findViewById(R.id.tvSumBranch);
        tvSumAppt = findViewById(R.id.tvSumAppt);
        tvSumPrice = findViewById(R.id.tvSumPrice);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        
        cardRecommendation = findViewById(R.id.cardRecommendation);
        tvRecBranchName = findViewById(R.id.tvRecBranchName);
        tvRecDistance = findViewById(R.id.tvRecDistance);
        tvRecTechnician = findViewById(R.id.tvRecTechnician);
        tvRecParts = findViewById(R.id.tvRecParts);
        tvRecReason = findViewById(R.id.tvRecReason);
        btnConfirmBranch = findViewById(R.id.btnConfirmBranch);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChooseGallery = findViewById(R.id.btnChooseGallery);
        rvRepairImages = findViewById(R.id.rvRepairImages);
        tvNoImages = findViewById(R.id.tvNoImages);

        imageAdapter = new RepairImageAdapter(new RepairImageAdapter.OnImageActionListener() {
            @Override
            public void onImageClick(SelectedImage image) {
                showImagePreviewDialog(image.getUri());
            }

            @Override
            public void onImageDelete(SelectedImage image, int position) {
                removeImage(position);
            }
        });

        rvRepairImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRepairImages.setAdapter(imageAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Default appointment: Tomorrow at 10:00 AM
        appointmentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        appointmentCalendar.set(Calendar.HOUR_OF_DAY, 10);
        appointmentCalendar.set(Calendar.MINUTE, 0);
        updateDateTimeText();
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> {
            if (currentStep == 4) {
                if (createdBookingReference != null) {
                    uploadRepairImages(createdBookingReference);
                } else {
                    submitBooking();
                }
            } else {
                if (validateStep(currentStep)) {
                    currentStep++;
                    updateStepUI();
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            if (currentStep > 1) {
                currentStep--;
                updateStepUI();
                // Reset booking reference to allow fresh submission if they edit
                createdBookingReference = null;
            }
        });

        btnConfirmBranch.setOnClickListener(v -> {
            isBranchConfirmed = true;
            btnConfirmBranch.setText("Branch Confirmed ✓");
            btnConfirmBranch.setEnabled(false);
            tvError.setVisibility(View.GONE);
            Toast.makeText(this, "Branch selection confirmed successfully.", Toast.LENGTH_SHORT).show();
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickTime.setOnClickListener(v -> showTimePicker());

        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        btnChooseGallery.setOnClickListener(v -> getContentLauncher.launch("image/*"));

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < categories.size()) {
                    selectedCategory = categories.get(position);
                    loadServicesForCategory(selectedCategory.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < services.size()) {
                    selectedService = services.get(position);
                    showSelectedServiceCard(selectedService);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateStepUI() {
        tvError.setVisibility(View.GONE);

        layoutStep1.setVisibility(currentStep == 1 ? View.VISIBLE : View.GONE);
        layoutStep2.setVisibility(currentStep == 2 ? View.VISIBLE : View.GONE);
        layoutStep3.setVisibility(currentStep == 3 ? View.VISIBLE : View.GONE);
        layoutStep4.setVisibility(currentStep == 4 ? View.VISIBLE : View.GONE);

        btnBack.setVisibility(currentStep > 1 ? View.VISIBLE : View.GONE);
        btnNext.setText(currentStep == 4 ? "Confirm & Submit" : "Next Step");

        // Indicators style update
        int activeColor = ContextCompat.getColor(this, R.color.primary);
        int mutedColor = ContextCompat.getColor(this, R.color.text_muted);

        step1Indicator.setTextColor(currentStep >= 1 ? activeColor : mutedColor);
        step2Indicator.setTextColor(currentStep >= 2 ? activeColor : mutedColor);
        step3Indicator.setTextColor(currentStep >= 3 ? activeColor : mutedColor);
        step4Indicator.setTextColor(currentStep >= 4 ? activeColor : mutedColor);

        if (currentStep == 3) {
            checkLocationAndQueryRecommendation();
        }

        if (currentStep == 4) {
            populateSummary();
        }
    }

    private boolean validateStep(int step) {
        if (step == 1) {
            if (selectedService == null) {
                showError("Please select a valid repair service");
                return false;
            }
        } else if (step == 2) {
            String brand = getText(etDeviceBrand);
            String model = getText(etDeviceModel);
            String desc = getText(etProblemDescription);

            if (TextUtils.isEmpty(brand)) {
                etDeviceBrand.setError("Device brand is required");
                return false;
            }
            if (TextUtils.isEmpty(model)) {
                etDeviceModel.setError("Device model is required");
                return false;
            }
            if (TextUtils.isEmpty(desc)) {
                etProblemDescription.setError("Problem description is required");
                return false;
            }
        } else if (step == 3) {
            if (!isBranchConfirmed) {
                showError("Please click [Confirm Branch] to verify your branch selection before proceeding.");
                return false;
            }
        }
        return true;
    }

    private void loadCategories() {
        showLoading(true);
        ApiService api = ApiClient.getApiService();

        api.getCategories().enqueue(new Callback<ApiResponse<List<DeviceCategoryDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DeviceCategoryDto>>> call, Response<ApiResponse<List<DeviceCategoryDto>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    categories = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    for (DeviceCategoryDto cat : categories) {
                        categoryNames.add(cat.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookRepairActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    showError("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DeviceCategoryDto>>> call, Throwable t) {
                showLoading(false);
                showError("Network error loading categories");
            }
        });
    }

    private void loadServicesForCategory(Long categoryId) {
        ApiService api = ApiClient.getApiService();
        api.getServices(categoryId).enqueue(new Callback<ApiResponse<List<RepairServiceDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RepairServiceDto>>> call, Response<ApiResponse<List<RepairServiceDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    services = response.body().getData();
                    List<String> serviceNames = new ArrayList<>();
                    int preSelectPos = -1;

                    for (int i = 0; i < services.size(); i++) {
                        RepairServiceDto s = services.get(i);
                        serviceNames.add(s.getName() + " (LKR " + String.format(Locale.getDefault(), "%,.0f", s.getEstimatedPrice()) + ")");
                        if (preSelectedServiceId != null && preSelectedServiceId.equals(s.getId())) {
                            preSelectPos = i;
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookRepairActivity.this,
                            android.R.layout.simple_spinner_item, serviceNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerService.setAdapter(adapter);

                    if (preSelectPos != -1) {
                        spinnerService.setSelection(preSelectPos);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RepairServiceDto>>> call, Throwable t) {}
        });
    }

    private void showSelectedServiceCard(RepairServiceDto service) {
        cardSelectedServiceInfo.setVisibility(View.VISIBLE);
        tvSelServiceName.setText(service.getName());
        tvSelServiceDesc.setText(service.getDescription());
        tvSelServicePrice.setText("Estimated Price: LKR " + String.format(Locale.getDefault(), "%,.2f", service.getEstimatedPrice()));
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            appointmentCalendar.set(Calendar.YEAR, year);
            appointmentCalendar.set(Calendar.MONTH, month);
            appointmentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateTimeText();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    private void showTimePicker() {
        TimePickerDialog tpd = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            appointmentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            appointmentCalendar.set(Calendar.MINUTE, minute);
            updateDateTimeText();
        }, appointmentCalendar.get(Calendar.HOUR_OF_DAY), appointmentCalendar.get(Calendar.MINUTE), false);
        tpd.show();
    }

    private void updateDateTimeText() {
        String formatted = String.format(Locale.getDefault(), "%tY-%tm-%td %tI:%tM %tp",
                appointmentCalendar, appointmentCalendar, appointmentCalendar,
                appointmentCalendar, appointmentCalendar, appointmentCalendar);
        tvSelectedDateTime.setText("Selected Appointment: " + formatted);
    }

    private void populateSummary() {
        if (selectedService != null) {
            tvSumService.setText("Service: " + selectedService.getName());
            tvSumPrice.setText("LKR " + String.format(Locale.getDefault(), "%,.2f", selectedService.getEstimatedPrice()));
        }
        tvSumDevice.setText("Device: " + getText(etDeviceBrand) + " " + getText(etDeviceModel));

        Long branchId = getSelectedBranchId();
        tvSumBranch.setText("Branch: " + (branchId == 1L ? "TechFix Colombo Branch" : "TechFix Galle Branch"));

        String apptStr = String.format(Locale.getDefault(), "%tY-%tm-%td %tI:%tM %tp",
                appointmentCalendar, appointmentCalendar, appointmentCalendar,
                appointmentCalendar, appointmentCalendar, appointmentCalendar);
        tvSumAppt.setText("Appointment: " + apptStr);
    }

    private Long getSelectedBranchId() {
        int selectedId = rgBranch.getCheckedRadioButtonId();
        if (selectedId == R.id.rbGalle) {
            return 2L; // Galle Branch
        }
        return 1L; // Colombo Branch (Default)
    }

    private void submitBooking() {
        showLoading(true);

        Long branchId = getSelectedBranchId();
        String brand = getText(etDeviceBrand);
        String model = getText(etDeviceModel);
        String serial = getText(etSerialNumber);
        String problem = getText(etProblemDescription);

        String apptIso = String.format(Locale.getDefault(), "%tY-%tm-%tdT%tH:%tM:00",
                appointmentCalendar, appointmentCalendar, appointmentCalendar,
                appointmentCalendar, appointmentCalendar);

        BookingRequestDto dto = new BookingRequestDto(
                selectedService.getId(),
                branchId,
                brand,
                model,
                serial,
                problem,
                apptIso
        );
        dto.setCustomerLatitude(customerLatitude);
        dto.setCustomerLongitude(customerLongitude);

        ApiService api = ApiClient.getApiService();
        api.createBooking(dto).enqueue(new Callback<ApiResponse<BookingResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingResponseDto>> call, Response<ApiResponse<BookingResponseDto>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BookingResponseDto booking = response.body().getData();
                    if (!selectedImages.isEmpty()) {
                        uploadRepairImages(booking.getBookingReference());
                    } else {
                        showSuccessDialog(booking.getBookingReference());
                    }
                } else {
                    String errorMsg = "Failed to submit booking.";
                    if (response.body() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingResponseDto>> call, Throwable t) {
                showLoading(false);
                showError("Network error. Failed to submit repair booking.");
            }
        });
    }

    private void showSuccessDialog(String bookingRef) {
        new AlertDialog.Builder(this)
                .setTitle("Booking Submitted!")
                .setMessage("Your repair request has been successfully created.\n\nBooking Reference Code:\n" + bookingRef + "\n\nYou can track the repair status anytime from your account.")
                .setCancelable(false)
                .setPositiveButton("Track Repair", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Close", (dialog, which) -> finish())
                .show();
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnNext.setEnabled(!loading);
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    // ─── GPS SMART BRANCH ALLOCATION ─────────────────────────────────

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndQueryRecommendation();
            } else {
                // Fallback coordinates (Dehiwala / Colombo Suburbs)
                customerLatitude = 6.8480;
                customerLongitude = 79.9265;
                queryRecommendation(customerLatitude, customerLongitude);
            }
        }
    }

    private void checkLocationAndQueryRecommendation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1002);
        } else {
            getLocationAndQueryRecommendation();
        }
    }

    private void getLocationAndQueryRecommendation() {
        try {
            showLoading(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    customerLatitude = location.getLatitude();
                    customerLongitude = location.getLongitude();
                } else {
                    // Fallback coordinates (Dehiwala / Colombo Suburbs)
                    customerLatitude = 6.8480;
                    customerLongitude = 79.9265;
                }
                queryRecommendation(customerLatitude, customerLongitude);
            });
        } catch (SecurityException e) {
            showLoading(false);
            e.printStackTrace();
            // Fallback coordinates (Dehiwala / Colombo Suburbs)
            customerLatitude = 6.8480;
            customerLongitude = 79.9265;
            queryRecommendation(customerLatitude, customerLongitude);
        }
    }

    private void queryRecommendation(double lat, double lon) {
        showLoading(true);
        ApiService api = ApiClient.getApiService();
        String brand = getText(etDeviceBrand);
        String model = getText(etDeviceModel);

        BranchRecommendationRequestDto req = new BranchRecommendationRequestDto(
                lat, lon, selectedService.getId(), brand, model
        );

        isBranchConfirmed = false;
        btnConfirmBranch.setText("Confirm Branch");
        btnConfirmBranch.setEnabled(true);

        api.recommendBranch(req).enqueue(new Callback<ApiResponse<BranchRecommendationResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<BranchRecommendationResponseDto>> call, @NonNull Response<ApiResponse<BranchRecommendationResponseDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BranchRecommendationResponseDto data = response.body().getData();
                    if (data != null && data.getRecommendedBranch() != null) {
                        cardRecommendation.setVisibility(View.VISIBLE);

                        String name = data.getRecommendedBranch().getName();
                        Double dist = data.getDistanceKm();
                        boolean techAvail = data.getIsTechnicianAvailable();
                        boolean partAvail = data.getIsPartAvailable();
                        String reason = data.getReason();

                        tvRecBranchName.setText("Recommended Branch: " + name);
                        tvRecDistance.setText(String.format(Locale.getDefault(), "Distance: %.1f km", dist));
                        
                        if (techAvail) {
                            tvRecTechnician.setText("Technician: Available");
                            tvRecTechnician.setTextColor(ContextCompat.getColor(BookRepairActivity.this, R.color.status_completed));
                        } else {
                            tvRecTechnician.setText("Technician: Busy / Unavailable");
                            tvRecTechnician.setTextColor(ContextCompat.getColor(BookRepairActivity.this, R.color.status_in_progress));
                        }

                        if (partAvail) {
                            tvRecParts.setText("Required Parts: Available");
                            tvRecParts.setTextColor(ContextCompat.getColor(BookRepairActivity.this, R.color.status_completed));
                        } else {
                            tvRecParts.setText("Required Parts: Out of Stock");
                            tvRecParts.setTextColor(ContextCompat.getColor(BookRepairActivity.this, R.color.status_cancelled));
                        }

                        tvRecReason.setText("Reason: " + reason);

                        Long recommendedId = data.getRecommendedBranch().getId();
                        if (recommendedId == 2L) {
                            rgBranch.check(R.id.rbGalle);
                        } else {
                            rgBranch.check(R.id.rbColombo);
                        }

                        // Validation: If no suitable branch is available, block next step navigation
                        if (!techAvail || !partAvail) {
                            showError("Booking cannot proceed. No suitable branch is currently available for this repair service.");
                            btnConfirmBranch.setEnabled(false);
                            btnConfirmBranch.setText("Unavailable");
                            isBranchConfirmed = false;
                        } else {
                            tvError.setVisibility(View.GONE);
                        }
                    } else {
                        cardRecommendation.setVisibility(View.GONE);
                        showError("Failed to fetch smart recommendation details.");
                    }
                } else {
                    cardRecommendation.setVisibility(View.GONE);
                    showError("Recommendation API failed: " + (response.body() != null ? response.body().getMessage() : "API Error"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<BranchRecommendationResponseDto>> call, @NonNull Throwable t) {
                showLoading(false);
                cardRecommendation.setVisibility(View.GONE);
                showError("Network error. Failed to retrieve smart branch recommendation.");
            }
        });
    }

    // ─── REPAIR IMAGES LOCAL SELECTION & PREVIEW ──────────────────────

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File storageDir = getExternalCacheDir() != null ? getExternalCacheDir() : getCacheDir();
            File photoFile = File.createTempFile("temp_repair_", ".jpg", storageDir);
            tempCameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            takePictureLauncher.launch(tempCameraUri);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to launch camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void removeImage(int position) {
        if (position >= 0 && position < selectedImages.size()) {
            selectedImages.remove(position);
            imageAdapter.setImageList(selectedImages);
            updateImagesStateUI();
        }
    }

    private void updateImagesStateUI() {
        if (selectedImages.isEmpty()) {
            rvRepairImages.setVisibility(View.GONE);
            tvNoImages.setVisibility(View.VISIBLE);
        } else {
            rvRepairImages.setVisibility(View.VISIBLE);
            tvNoImages.setVisibility(View.GONE);
        }
    }

    private void showImagePreviewDialog(Uri uri) {
        final android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_preview);
        ImageView ivPreview = dialog.findViewById(R.id.ivPreview);
        View btnClose = dialog.findViewById(R.id.btnClosePreview);

        Glide.with(this)
                .load(uri)
                .into(ivPreview);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private okhttp3.MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            android.content.ContentResolver contentResolver = getContentResolver();
            String type = contentResolver.getType(fileUri);
            if (type == null) {
                type = "image/jpeg";
            }

            java.io.InputStream inputStream = contentResolver.openInputStream(fileUri);
            if (inputStream == null) return null;

            java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            byte[] bytes = byteBuffer.toByteArray();
            inputStream.close();

            okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse(type),
                    bytes
            );

            String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
            android.database.Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }

            return okhttp3.MultipartBody.Part.createFormData(partName, fileName, requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadRepairImages(String bookingRef) {
        createdBookingReference = bookingRef;

        final List<SelectedImage> toUpload = new ArrayList<>();
        for (SelectedImage img : selectedImages) {
            if (img.getState() != SelectedImage.State.UPLOADED) {
                toUpload.add(img);
            }
        }

        if (toUpload.isEmpty()) {
            showSuccessDialog(bookingRef);
            return;
        }

        // Show a progress dialog
        uploadProgressDialog = new android.app.ProgressDialog(this);
        uploadProgressDialog.setTitle("Uploading Images");
        uploadProgressDialog.setMessage("Uploading 1 of " + toUpload.size() + "...");
        uploadProgressDialog.setCancelable(false);
        uploadProgressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        uploadProgressDialog.show();

        uploadNextImage(bookingRef, toUpload, 0);
    }

    private void uploadNextImage(String bookingRef, List<SelectedImage> toUpload, int index) {
        if (index >= toUpload.size()) {
            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                uploadProgressDialog.dismiss();
            }

            boolean hasFailed = false;
            for (SelectedImage img : selectedImages) {
                if (img.getState() == SelectedImage.State.FAILED) {
                    hasFailed = true;
                    break;
                }
            }

            if (hasFailed) {
                currentStep = 2;
                updateStepUI();
                showError("Some images failed to upload. You can review and retry from the images section.");
                Toast.makeText(this, "Failed to upload some images. Please retry.", Toast.LENGTH_LONG).show();
            } else {
                showSuccessDialog(bookingRef);
            }
            return;
        }

        SelectedImage currentImg = toUpload.get(index);
        currentImg.setState(SelectedImage.State.UPLOADING);
        imageAdapter.notifyDataSetChanged();

        if (uploadProgressDialog != null) {
            uploadProgressDialog.setMessage("Uploading " + (index + 1) + " of " + toUpload.size() + "...");
        }

        okhttp3.MultipartBody.Part filePart = prepareFilePart("file", currentImg.getUri());
        if (filePart == null) {
            currentImg.setState(SelectedImage.State.FAILED);
            imageAdapter.notifyDataSetChanged();
            uploadNextImage(bookingRef, toUpload, index + 1);
            return;
        }

        okhttp3.RequestBody refBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                bookingRef
        );
        okhttp3.RequestBody typeBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                "CUSTOMER_DAMAGE"
        );

        ApiService api = ApiClient.getApiService();
        api.uploadRepairImage(filePart, refBody, typeBody).enqueue(new Callback<ApiResponse<com.techfix.app.data.remote.dto.RepairImageDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<com.techfix.app.data.remote.dto.RepairImageDto>> call, @NonNull Response<ApiResponse<com.techfix.app.data.remote.dto.RepairImageDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentImg.setState(SelectedImage.State.UPLOADED);
                } else {
                    currentImg.setState(SelectedImage.State.FAILED);
                }
                imageAdapter.notifyDataSetChanged();
                uploadNextImage(bookingRef, toUpload, index + 1);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<com.techfix.app.data.remote.dto.RepairImageDto>> call, @NonNull Throwable t) {
                currentImg.setState(SelectedImage.State.FAILED);
                imageAdapter.notifyDataSetChanged();
                uploadNextImage(bookingRef, toUpload, index + 1);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> uriStrings = new ArrayList<>();
        for (SelectedImage img : selectedImages) {
            uriStrings.add(img.getUri().toString());
        }
        outState.putStringArrayList("selected_image_uris", uriStrings);
        if (tempCameraUri != null) {
            outState.putString("temp_camera_uri", tempCameraUri.toString());
        }
        if (createdBookingReference != null) {
            outState.putString("created_booking_reference", createdBookingReference);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> uriStrings = savedInstanceState.getStringArrayList("selected_image_uris");
        if (uriStrings != null) {
            selectedImages.clear();
            for (String s : uriStrings) {
                selectedImages.add(new SelectedImage(Uri.parse(s)));
            }
            if (imageAdapter != null) {
                imageAdapter.setImageList(selectedImages);
                updateImagesStateUI();
            }
        }
        String tempUriStr = savedInstanceState.getString("temp_camera_uri");
        if (tempUriStr != null) {
            tempCameraUri = Uri.parse(tempUriStr);
        }
        createdBookingReference = savedInstanceState.getString("created_booking_reference");
    }
}
