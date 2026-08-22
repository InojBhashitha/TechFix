package com.techfix.app.data.remote.dto;

import java.math.BigDecimal;

public class UpdateRepairStatusRequestDto {
    private String status;
    private String notes;
    private BigDecimal additionalCost;

    public UpdateRepairStatusRequestDto() {}

    public UpdateRepairStatusRequestDto(String status, String notes, BigDecimal additionalCost) {
        this.status = status;
        this.notes = notes;
        this.additionalCost = additionalCost;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(BigDecimal additionalCost) { this.additionalCost = additionalCost; }
}
