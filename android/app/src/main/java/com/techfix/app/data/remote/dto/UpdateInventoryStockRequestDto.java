package com.techfix.app.data.remote.dto;

public class UpdateInventoryStockRequestDto {
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
