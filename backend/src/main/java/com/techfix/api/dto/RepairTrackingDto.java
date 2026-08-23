package com.techfix.api.dto;

import com.techfix.api.enums.RepairStatus;
import java.math.BigDecimal;
import java.util.List;

public class RepairTrackingDto {
    private String bookingReference;
    private String serviceName;
    private String branchName;
    private String deviceBrand;
    private String deviceModel;
    private String problemDescription;
    private RepairStatus currentStatus;
    private String currentStatusDisplayName;
    private BigDecimal totalCost;
    private List<RepairStatusHistoryDto> history;

    public RepairTrackingDto() {}

    public RepairTrackingDto(String bookingReference, String serviceName, String branchName, String deviceBrand, String deviceModel, String problemDescription, RepairStatus currentStatus, String currentStatusDisplayName, BigDecimal totalCost, List<RepairStatusHistoryDto> history) {
        this.bookingReference = bookingReference;
        this.serviceName = serviceName;
        this.branchName = branchName;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.currentStatus = currentStatus;
        this.currentStatusDisplayName = currentStatusDisplayName;
        this.totalCost = totalCost;
        this.history = history;
    }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getDeviceBrand() { return deviceBrand; }
    public void setDeviceBrand(String deviceBrand) { this.deviceBrand = deviceBrand; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public RepairStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(RepairStatus currentStatus) { this.currentStatus = currentStatus; }

    public String getCurrentStatusDisplayName() { return currentStatusDisplayName; }
    public void setCurrentStatusDisplayName(String currentStatusDisplayName) { this.currentStatusDisplayName = currentStatusDisplayName; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public List<RepairStatusHistoryDto> getHistory() { return history; }
    public void setHistory(List<RepairStatusHistoryDto> history) { this.history = history; }
}
