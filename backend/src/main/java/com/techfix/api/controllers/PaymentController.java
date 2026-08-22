package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.dto.PaymentCheckoutRequestDto;
import com.techfix.api.dto.PaymentReceiptDto;
import com.techfix.api.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<PaymentReceiptDto>> checkout(@RequestBody PaymentCheckoutRequestDto request) {
        try {
            PaymentReceiptDto receipt = paymentService.processCheckout(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Payment processed successfully", receipt));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/receipt/{transactionReference}")
    public ResponseEntity<ApiResponse<PaymentReceiptDto>> getReceiptByTransaction(@PathVariable String transactionReference) {
        try {
            PaymentReceiptDto receipt = paymentService.getReceiptByTransactionReference(transactionReference);
            return ResponseEntity.ok(ApiResponse.success("Receipt retrieved successfully", receipt));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingReference}")
    public ResponseEntity<ApiResponse<PaymentReceiptDto>> getReceiptByBooking(@PathVariable String bookingReference) {
        try {
            PaymentReceiptDto receipt = paymentService.getReceiptByBookingReference(bookingReference);
            return ResponseEntity.ok(ApiResponse.success("Receipt retrieved successfully", receipt));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
