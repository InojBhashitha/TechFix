package com.techfix.app.ui.payment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.data.remote.dto.ApiResponse;
import com.techfix.app.data.remote.dto.PaymentCheckoutRequestDto;
import com.techfix.app.data.remote.dto.PaymentReceiptDto;

import java.math.BigDecimal;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvBookingReference;
    private TextView tvBranchName;
    private TextView tvServiceName;
    private TextView tvDeviceInfo;
    private TextView tvTotalCost;

    private RadioGroup rgPaymentMethods;
    private RadioButton radioCard;
    private RadioButton radioCash;
    private RadioButton radioOnlineDemo;

    private MaterialCardView containerCardInputs;
    private TextInputEditText etCardNumber;
    private TextInputEditText etCardExpiry;
    private TextInputEditText etCardCvv;

    private ProgressBar progressPayment;
    private MaterialButton btnPayNow;

    private String bookingRef;
    private String serviceName;
    private String deviceInfo;
    private String branchName;
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        parseIntentExtras();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvBookingReference = findViewById(R.id.tv_booking_reference);
        tvBranchName = findViewById(R.id.tv_branch_name);
        tvServiceName = findViewById(R.id.tv_service_name);
        tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvTotalCost = findViewById(R.id.tv_total_cost);

        rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        radioCard = findViewById(R.id.radio_card);
        radioCash = findViewById(R.id.radio_cash);
        radioOnlineDemo = findViewById(R.id.radio_online_demo);

        containerCardInputs = findViewById(R.id.container_card_inputs);
        etCardNumber = findViewById(R.id.et_card_number);
        etCardExpiry = findViewById(R.id.et_card_expiry);
        etCardCvv = findViewById(R.id.et_card_cvv);

        progressPayment = findViewById(R.id.progress_payment);
        btnPayNow = findViewById(R.id.btn_pay_now);
    }

    private void parseIntentExtras() {
        bookingRef = getIntent().getStringExtra("bookingRef");
        serviceName = getIntent().getStringExtra("serviceName");
        deviceInfo = getIntent().getStringExtra("deviceInfo");
        branchName = getIntent().getStringExtra("branchName");

        double costExtra = getIntent().getDoubleExtra("totalCost", 0.0);
        totalCost = BigDecimal.valueOf(costExtra);

        if (bookingRef != null && !bookingRef.isEmpty()) {
            tvBookingReference.setText(bookingRef);
        } else {
            tvBookingReference.setText("TF-2026-DEMO");
            bookingRef = "TF-2026-DEMO";
        }

        if (branchName != null && !branchName.isEmpty()) {
            tvBranchName.setText(branchName);
        }

        if (serviceName != null && !serviceName.isEmpty()) {
            tvServiceName.setText(serviceName);
        }

        if (deviceInfo != null && !deviceInfo.isEmpty()) {
            tvDeviceInfo.setText("Device: " + deviceInfo);
        }

        tvTotalCost.setText(String.format(Locale.getDefault(), "LKR %.2f", totalCost));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_card) {
                containerCardInputs.setVisibility(View.VISIBLE);
                btnPayNow.setText("Pay LKR " + String.format(Locale.getDefault(), "%.2f", totalCost));
            } else if (checkedId == R.id.radio_cash) {
                containerCardInputs.setVisibility(View.GONE);
                btnPayNow.setText("Confirm Cash on Collection");
            } else {
                containerCardInputs.setVisibility(View.GONE);
                btnPayNow.setText("Simulate Online Payment");
            }
        });

        btnPayNow.setOnClickListener(v -> handlePaymentSubmit());
    }

    private void handlePaymentSubmit() {
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();
        String paymentMethod;
        String cardNumber = null;
        String cardExpiry = null;
        String cardCvv = null;

        if (selectedId == R.id.radio_card) {
            paymentMethod = "CARD";
            cardNumber = etCardNumber.getText() != null ? etCardNumber.getText().toString().trim() : "";
            cardExpiry = etCardExpiry.getText() != null ? etCardExpiry.getText().toString().trim() : "";
            cardCvv = etCardCvv.getText() != null ? etCardCvv.getText().toString().trim() : "";

            if (TextUtils.isEmpty(cardNumber) || cardNumber.replaceAll("\\s+", "").length() < 12) {
                etCardNumber.setError("Enter a valid 16-digit card number");
                return;
            }
            if (TextUtils.isEmpty(cardExpiry) || !cardExpiry.contains("/")) {
                etCardExpiry.setError("Enter valid expiry (MM/YY)");
                return;
            }
            if (TextUtils.isEmpty(cardCvv) || cardCvv.length() < 3) {
                etCardCvv.setError("Enter valid 3-digit CVV");
                return;
            }
        } else if (selectedId == R.id.radio_cash) {
            paymentMethod = "CASH";
        } else {
            paymentMethod = "ONLINE_DEMO";
        }

        progressPayment.setVisibility(View.VISIBLE);
        btnPayNow.setEnabled(false);

        PaymentCheckoutRequestDto request = new PaymentCheckoutRequestDto(
                bookingRef,
                null,
                paymentMethod,
                totalCost,
                cardNumber,
                cardExpiry,
                cardCvv
        );

        ApiClient.getApiService().checkoutPayment(request).enqueue(new Callback<ApiResponse<PaymentReceiptDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PaymentReceiptDto>> call, @NonNull Response<ApiResponse<PaymentReceiptDto>> response) {
                progressPayment.setVisibility(View.GONE);
                btnPayNow.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaymentReceiptDto receipt = response.body().getData();
                    showReceiptDialog(receipt);
                } else {
                    String errorMsg = "Payment processing failed";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(PaymentActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PaymentReceiptDto>> call, @NonNull Throwable t) {
                progressPayment.setVisibility(View.GONE);
                btnPayNow.setEnabled(true);
                Toast.makeText(PaymentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showReceiptDialog(PaymentReceiptDto receipt) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_digital_receipt, null);

        TextView tvStatusBadge = dialogView.findViewById(R.id.tv_receipt_status_badge);
        TextView tvTxnRef = dialogView.findViewById(R.id.tv_receipt_txn_ref);
        TextView tvBookingRef = dialogView.findViewById(R.id.tv_receipt_booking_ref);
        TextView tvCustomerName = dialogView.findViewById(R.id.tv_receipt_customer_name);
        TextView tvServiceDevice = dialogView.findViewById(R.id.tv_receipt_service_device);
        TextView tvPaymentMethod = dialogView.findViewById(R.id.tv_receipt_payment_method);
        TextView tvDate = dialogView.findViewById(R.id.tv_receipt_date);
        TextView tvTotalAmount = dialogView.findViewById(R.id.tv_receipt_total_amount);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_close_receipt);

        if (receipt != null) {
            if ("PAID".equalsIgnoreCase(receipt.getPaymentStatus())) {
                tvStatusBadge.setText("PAID");
            } else {
                tvStatusBadge.setText(receipt.getPaymentStatus() != null ? receipt.getPaymentStatus() : "PENDING");
            }

            tvTxnRef.setText(receipt.getTransactionReference() != null ? receipt.getTransactionReference() : "N/A");
            tvBookingRef.setText(receipt.getBookingReference() != null ? receipt.getBookingReference() : bookingRef);
            tvCustomerName.setText(receipt.getCustomerName() != null ? receipt.getCustomerName() : "Valued Customer");

            String sDev = (receipt.getDeviceModel() != null ? receipt.getDeviceModel() : "") + " - " + (receipt.getServiceName() != null ? receipt.getServiceName() : "");
            tvServiceDevice.setText(sDev);

            tvPaymentMethod.setText(receipt.getPaymentMethod() != null ? receipt.getPaymentMethod() : "CARD");
            tvDate.setText(receipt.getPaidAt() != null ? receipt.getPaidAt() : (receipt.getCreatedAt() != null ? receipt.getCreatedAt() : "Today"));
            tvTotalAmount.setText(String.format(Locale.getDefault(), "LKR %.2f", receipt.getAmount() != null ? receipt.getAmount() : totalCost));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}
