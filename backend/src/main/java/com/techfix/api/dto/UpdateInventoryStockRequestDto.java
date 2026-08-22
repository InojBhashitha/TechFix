package com.techfix.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateInventoryStockRequestDto {

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    private String reason;

    public UpdateInventoryStockRequestDto() {}

    public UpdateInventoryStockRequestDto(Integer quantity, Integer reorderLevel, String reason) {
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.reason = reason;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
