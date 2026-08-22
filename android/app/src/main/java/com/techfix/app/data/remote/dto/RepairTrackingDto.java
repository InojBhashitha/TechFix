package com.techfix.app.data.remote.dto;

import java.util.List;

public class RepairTrackingDto {
    private String bookingReference;
    private String deviceBrand;
    private String deviceModel;
    private String problemDescription;
    private String currentStatus;
    private String currentStatusDisplayName;
    private List<RepairStatusHistoryDto> history;

    public RepairTrackingDto() {}

    public String getBookingReference() { return bookingReference; }
    public String getDeviceBrand() { return deviceBrand; }
    public String getDeviceModel() { return deviceModel; }
    public String getProblemDescription() { return problemDescription; }
    public String getCurrentStatus() { return currentStatus; }
    public String getCurrentStatusDisplayName() { return currentStatusDisplayName; }
    public List<RepairStatusHistoryDto> getHistory() { return history; }
}
