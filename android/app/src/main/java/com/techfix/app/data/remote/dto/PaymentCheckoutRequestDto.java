package com.techfix.app.data.remote.dto;

import java.math.BigDecimal;

public class PaymentCheckoutRequestDto {

    private String bookingReference;
    private Long repairRequestId;
    private String paymentMethod;
    private BigDecimal amount;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;

    public PaymentCheckoutRequestDto() {}

    public PaymentCheckoutRequestDto(String bookingReference, Long repairRequestId, String paymentMethod, BigDecimal amount, String cardNumber, String cardExpiry, String cardCvv) {
        this.bookingReference = bookingReference;
        this.repairRequestId = repairRequestId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCvv = cardCvv;
    }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getRepairRequestId() { return repairRequestId; }
    public void setRepairRequestId(Long repairRequestId) { this.repairRequestId = repairRequestId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }
}
