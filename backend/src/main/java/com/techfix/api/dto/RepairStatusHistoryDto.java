package com.techfix.api.dto;

import com.techfix.api.enums.RepairStatus;
import java.time.LocalDateTime;

public class RepairStatusHistoryDto {
    private RepairStatus status;
    private String statusDisplayName;
    private String notes;
    private String updatedByName;
    private LocalDateTime timestamp;

    public RepairStatusHistoryDto() {}

    public RepairStatusHistoryDto(RepairStatus status, String statusDisplayName, String notes, String updatedByName, LocalDateTime timestamp) {
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.notes = notes;
        this.updatedByName = updatedByName;
        this.timestamp = timestamp;
    }

    public RepairStatus getStatus() { return status; }
    public void setStatus(RepairStatus status) { this.status = status; }

    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
