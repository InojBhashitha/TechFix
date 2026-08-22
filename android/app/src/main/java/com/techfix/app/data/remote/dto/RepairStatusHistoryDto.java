package com.techfix.app.data.remote.dto;

public class RepairStatusHistoryDto {
    private String status;
    private String statusDisplayName;
    private String notes;
    private String updatedByName;
    private String timestamp;

    public RepairStatusHistoryDto() {}

    public String getStatus() { return status; }
    public String getStatusDisplayName() { return statusDisplayName; }
    public String getNotes() { return notes; }
    public String getUpdatedByName() { return updatedByName; }
    public String getTimestamp() { return timestamp; }
}
