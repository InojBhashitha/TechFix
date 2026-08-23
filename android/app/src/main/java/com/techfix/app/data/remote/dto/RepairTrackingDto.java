package com.techfix.app.data.remote.dto;

import java.math.BigDecimal;
import java.util.List;

public class RepairTrackingDto {
    private String bookingReference;
    private String serviceName;
    private String branchName;
    private String deviceBrand;
    private String deviceModel;
    private String problemDescription;
    private String currentStatus;
    private String currentStatusDisplayName;
    private BigDecimal totalCost;
    private Boolean isPaid;
    private String paymentStatus;
    private List<RepairStatusHistoryDto> history;

    public RepairTrackingDto() {}

    public String getBookingReference() { return bookingReference; }
    public String getServiceName() { return serviceName; }
    public String getBranchName() { return branchName; }
    public String getDeviceBrand() { return deviceBrand; }
    public String getDeviceModel() { return deviceModel; }
    public String getProblemDescription() { return problemDescription; }
    public String getCurrentStatus() { return currentStatus; }
    public String getCurrentStatusDisplayName() { return currentStatusDisplayName; }
    public BigDecimal getTotalCost() { return totalCost; }
    public Boolean getIsPaid() { return isPaid; }
    public String getPaymentStatus() { return paymentStatus; }
    public List<RepairStatusHistoryDto> getHistory() { return history; }
}
