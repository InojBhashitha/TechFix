package com.techfix.api.dto;

import com.techfix.api.enums.PaymentMethod;
import java.math.BigDecimal;

public class PaymentCheckoutRequestDto {

    private String bookingReference;
    private Long repairRequestId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;

    public PaymentCheckoutRequestDto() {}

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getRepairRequestId() { return repairRequestId; }
    public void setRepairRequestId(Long repairRequestId) { this.repairRequestId = repairRequestId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }
}
