package com.techfix.api.dto;

import com.techfix.api.enums.RepairStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UpdateRepairStatusRequestDto {

    @NotNull(message = "Status is required")
    private RepairStatus status;

    private String notes;

    private BigDecimal additionalCost;

    private Long sparePartId;

    private Integer sparePartQuantity;

    public UpdateRepairStatusRequestDto() {}

    public UpdateRepairStatusRequestDto(RepairStatus status, String notes, BigDecimal additionalCost) {
        this.status = status;
        this.notes = notes;
        this.additionalCost = additionalCost;
    }

    public UpdateRepairStatusRequestDto(RepairStatus status, String notes, BigDecimal additionalCost, Long sparePartId, Integer sparePartQuantity) {
        this.status = status;
        this.notes = notes;
        this.additionalCost = additionalCost;
        this.sparePartId = sparePartId;
        this.sparePartQuantity = sparePartQuantity;
    }

    public RepairStatus getStatus() { return status; }
    public void setStatus(RepairStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(BigDecimal additionalCost) { this.additionalCost = additionalCost; }

    public Long getSparePartId() { return sparePartId; }
    public void setSparePartId(Long sparePartId) { this.sparePartId = sparePartId; }

    public Integer getSparePartQuantity() { return sparePartQuantity; }
    public void setSparePartQuantity(Integer sparePartQuantity) { this.sparePartQuantity = sparePartQuantity; }
}
