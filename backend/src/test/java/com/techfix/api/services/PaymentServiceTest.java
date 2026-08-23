package com.techfix.api.services;

import com.techfix.api.dto.PaymentCheckoutRequestDto;
import com.techfix.api.dto.PaymentReceiptDto;
import com.techfix.api.entities.Branch;
import com.techfix.api.entities.Payment;
import com.techfix.api.entities.RepairRequest;
import com.techfix.api.entities.User;
import com.techfix.api.enums.PaymentMethod;
import com.techfix.api.enums.PaymentStatus;
import com.techfix.api.repositories.PaymentRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @InjectMocks
    private PaymentService paymentService;

    private RepairRequest sampleRepairRequest;
    private User sampleCustomer;
    private Branch sampleBranch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleCustomer = new User();
        sampleCustomer.setId(1L);
        sampleCustomer.setFullName("John Doe");
        sampleCustomer.setEmail("john.doe@example.com");

        sampleBranch = new Branch();
        sampleBranch.setId(1L);
        sampleBranch.setName("Colombo Service Center");

        sampleRepairRequest = new RepairRequest();
        sampleRepairRequest.setId(10L);
        sampleRepairRequest.setBookingReference("TF-2026-1001");
        sampleRepairRequest.setCustomer(sampleCustomer);
        sampleRepairRequest.setBranch(sampleBranch);
        sampleRepairRequest.setDeviceBrand("Apple");
        sampleRepairRequest.setDeviceModel("iPhone 14");
        sampleRepairRequest.setTotalCost(new BigDecimal("150.00"));
    }

    @Test
    void testProcessCheckout_CardSuccess() {
        PaymentCheckoutRequestDto request = new PaymentCheckoutRequestDto();
        request.setBookingReference("TF-2026-1001");
        request.setPaymentMethod(PaymentMethod.CARD);
        request.setAmount(new BigDecimal("150.00"));
        request.setCardNumber("4111111111111111");
        request.setCardExpiry("12/28");
        request.setCardCvv("123");

        when(repairRequestRepository.findByBookingReference("TF-2026-1001"))
                .thenReturn(Optional.of(sampleRepairRequest));
        when(paymentRepository.findByRepairRequestId(10L))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PaymentReceiptDto receipt = paymentService.processCheckout(request);

        assertNotNull(receipt);
        assertEquals("TF-2026-1001", receipt.getBookingReference());
        assertEquals(PaymentStatus.PAID, receipt.getPaymentStatus());
        assertEquals(PaymentMethod.CARD, receipt.getPaymentMethod());
        assertEquals(new BigDecimal("150.00"), receipt.getAmount());
        assertNotNull(receipt.getTransactionReference());
        assertTrue(receipt.getTransactionReference().startsWith("TXN-"));
        assertNotNull(receipt.getPaidAt());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(repairRequestRepository, times(1)).save(sampleRepairRequest);
    }

    @Test
    void testProcessCheckout_CashSuccess() {
        PaymentCheckoutRequestDto request = new PaymentCheckoutRequestDto();
        request.setBookingReference("TF-2026-1001");
        request.setPaymentMethod(PaymentMethod.CASH);
        request.setAmount(new BigDecimal("150.00"));

        when(repairRequestRepository.findByBookingReference("TF-2026-1001"))
                .thenReturn(Optional.of(sampleRepairRequest));
        when(paymentRepository.findByRepairRequestId(10L))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(101L);
            return p;
        });

        PaymentReceiptDto receipt = paymentService.processCheckout(request);

        assertNotNull(receipt);
        assertEquals(PaymentStatus.PENDING, receipt.getPaymentStatus());
        assertEquals(PaymentMethod.CASH, receipt.getPaymentMethod());
        assertNull(receipt.getPaidAt());
    }

    @Test
    void testProcessCheckout_BookingNotFound() {
        PaymentCheckoutRequestDto request = new PaymentCheckoutRequestDto();
        request.setBookingReference("TF-9999-9999");
        request.setPaymentMethod(PaymentMethod.ONLINE_DEMO);

        when(repairRequestRepository.findByBookingReference("TF-9999-9999"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.processCheckout(request));
    }

    @Test
    void testGetReceiptByTransactionReference_Success() {
        Payment payment = new Payment();
        payment.setId(200L);
        payment.setTransactionReference("TXN-12345678");
        payment.setAmount(new BigDecimal("200.00"));
        payment.setPaymentMethod(PaymentMethod.ONLINE_DEMO);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setRepairRequest(sampleRepairRequest);

        when(paymentRepository.findByTransactionReference("TXN-12345678"))
                .thenReturn(Optional.of(payment));

        PaymentReceiptDto receipt = paymentService.getReceiptByTransactionReference("TXN-12345678");

        assertNotNull(receipt);
        assertEquals("TXN-12345678", receipt.getTransactionReference());
        assertEquals("TF-2026-1001", receipt.getBookingReference());
        assertEquals(PaymentStatus.PAID, receipt.getPaymentStatus());
    }
}
