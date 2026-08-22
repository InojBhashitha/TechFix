package com.techfix.api.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateInventoryStockRequestDto {

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private Integer minimumStockAlert;

    public UpdateInventoryStockRequestDto() {}

    public UpdateInventoryStockRequestDto(Integer quantity, Integer minimumStockAlert) {
        this.quantity = quantity;
        this.minimumStockAlert = minimumStockAlert;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getMinimumStockAlert() { return minimumStockAlert; }
    public void setMinimumStockAlert(Integer minimumStockAlert) { this.minimumStockAlert = minimumStockAlert; }
}
