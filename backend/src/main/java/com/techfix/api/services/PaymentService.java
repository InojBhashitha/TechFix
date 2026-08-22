package com.techfix.api.services;

import com.techfix.api.dto.PaymentCheckoutRequestDto;
import com.techfix.api.dto.PaymentReceiptDto;
import com.techfix.api.entities.Payment;
import com.techfix.api.entities.RepairRequest;
import com.techfix.api.enums.PaymentMethod;
import com.techfix.api.enums.PaymentStatus;
import com.techfix.api.repositories.PaymentRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RepairRequestRepository repairRequestRepository;

    public PaymentService(PaymentRepository paymentRepository, RepairRequestRepository repairRequestRepository) {
        this.paymentRepository = paymentRepository;
        this.repairRequestRepository = repairRequestRepository;
    }

    @Transactional
    public PaymentReceiptDto processCheckout(PaymentCheckoutRequestDto request) {
        RepairRequest repairRequest = findRepairRequest(request);

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            amount = repairRequest.getTotalCost() != null ? repairRequest.getTotalCost() : BigDecimal.ZERO;
        }

        PaymentMethod paymentMethod = request.getPaymentMethod();
        if (paymentMethod == null) {
            paymentMethod = PaymentMethod.CARD;
        }

        Payment payment = paymentRepository.findByRepairRequestId(repairRequest.getId())
                .orElse(new Payment());

        payment.setRepairRequest(repairRequest);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);

        if (paymentMethod == PaymentMethod.CASH) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setPaidAt(null);
        } else {
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
        }

        if (payment.getTransactionReference() == null || payment.getTransactionReference().isEmpty()) {
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        Payment savedPayment = paymentRepository.save(payment);
        repairRequest.setPayment(savedPayment);
        repairRequestRepository.save(repairRequest);

        return mapToReceiptDto(savedPayment, repairRequest);
    }

    @Transactional(readOnly = true)
    public PaymentReceiptDto getReceiptByTransactionReference(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new RuntimeException("Payment not found for reference: " + transactionReference));
        return mapToReceiptDto(payment, payment.getRepairRequest());
    }

    @Transactional(readOnly = true)
    public PaymentReceiptDto getReceiptByBookingReference(String bookingReference) {
        RepairRequest repairRequest = repairRequestRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingReference));

        Payment payment = paymentRepository.findByRepairRequestId(repairRequest.getId())
                .orElseThrow(() -> new RuntimeException("Payment record not found for booking: " + bookingReference));

        return mapToReceiptDto(payment, repairRequest);
    }

    private RepairRequest findRepairRequest(PaymentCheckoutRequestDto request) {
        if (request.getBookingReference() != null && !request.getBookingReference().trim().isEmpty()) {
            return repairRequestRepository.findByBookingReference(request.getBookingReference().trim())
                    .orElseThrow(() -> new RuntimeException("Booking not found: " + request.getBookingReference()));
        } else if (request.getRepairRequestId() != null) {
            return repairRequestRepository.findById(request.getRepairRequestId())
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + request.getRepairRequestId()));
        } else {
            throw new IllegalArgumentException("Either bookingReference or repairRequestId must be provided");
        }
    }

    private PaymentReceiptDto mapToReceiptDto(Payment payment, RepairRequest repairRequest) {
        PaymentReceiptDto dto = new PaymentReceiptDto();
        dto.setPaymentId(payment.getId());
        dto.setTransactionReference(payment.getTransactionReference());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaidAt(payment.getPaidAt());
        dto.setCreatedAt(payment.getCreatedAt());

        if (repairRequest != null) {
            dto.setBookingReference(repairRequest.getBookingReference());

            if (repairRequest.getCustomer() != null) {
                dto.setCustomerName(repairRequest.getCustomer().getFullName());
                dto.setCustomerEmail(repairRequest.getCustomer().getEmail());
            }

            dto.setDeviceModel(repairRequest.getDeviceBrand() + " " + repairRequest.getDeviceModel());

            if (repairRequest.getService() != null) {
                dto.setServiceName(repairRequest.getService().getName());
            }

            if (repairRequest.getBranch() != null) {
                dto.setBranchName(repairRequest.getBranch().getName());
            }
        }

        return dto;
    }
}
