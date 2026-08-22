package com.techfix.api.dto;

import com.techfix.api.enums.RepairStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateRepairStatusRequestDto {

    @NotNull(message = "Status is required")
    private RepairStatus status;

    private String notes;

    private String updatedBy;

    public UpdateRepairStatusRequestDto() {}

    public UpdateRepairStatusRequestDto(RepairStatus status, String notes, String updatedBy) {
        this.status = status;
        this.notes = notes;
        this.updatedBy = updatedBy;
    }

    public RepairStatus getStatus() { return status; }
    public void setStatus(RepairStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
