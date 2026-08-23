package com.techfix.app.data.remote.dto;

import java.math.BigDecimal;

public class BookingResponseDto {
    private Long id;
    private String bookingReference;
    private String customerName;
    private String customerEmail;
    private String branchName;
    private String serviceName;
    private String categoryName;
    private String deviceBrand;
    private String deviceModel;
    private String serialNumber;
    private String problemDescription;
    private String appointmentDate;
    private String currentStatus;
    private String statusDisplayName;
    private Long technicianId;
    private String technicianName;
    private BigDecimal totalCost;
    private Boolean isPaid;
    private String paymentStatus;
    private String createdAt;

    public Long getId() { return id; }
    public String getBookingReference() { return bookingReference; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getBranchName() { return branchName; }
    public String getServiceName() { return serviceName; }
    public String getCategoryName() { return categoryName; }
    public String getDeviceBrand() { return deviceBrand; }
    public String getDeviceModel() { return deviceModel; }
    public String getSerialNumber() { return serialNumber; }
    public String getProblemDescription() { return problemDescription; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getCurrentStatus() { return currentStatus; }
    public String getStatusDisplayName() { return statusDisplayName; }
    public Long getTechnicianId() { return technicianId; }
    public String getTechnicianName() { return technicianName; }
    public BigDecimal getTotalCost() { return totalCost; }
    public Boolean getIsPaid() { return isPaid; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getCreatedAt() { return createdAt; }
}
