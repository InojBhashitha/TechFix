package com.techfix.api.dto;

import com.techfix.api.enums.RepairStatus;
import java.util.List;

public class RepairTrackingDto {
    private String bookingReference;
    private String deviceBrand;
    private String deviceModel;
    private String problemDescription;
    private RepairStatus currentStatus;
    private String currentStatusDisplayName;
    private List<RepairStatusHistoryDto> history;

    public RepairTrackingDto() {}

    public RepairTrackingDto(String bookingReference, String deviceBrand, String deviceModel, String problemDescription, RepairStatus currentStatus, String currentStatusDisplayName, List<RepairStatusHistoryDto> history) {
        this.bookingReference = bookingReference;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.currentStatus = currentStatus;
        this.currentStatusDisplayName = currentStatusDisplayName;
        this.history = history;
    }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

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

    public List<RepairStatusHistoryDto> getHistory() { return history; }
    public void setHistory(List<RepairStatusHistoryDto> history) { this.history = history; }
}
