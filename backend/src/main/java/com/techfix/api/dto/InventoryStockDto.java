package com.techfix.api.dto;

import java.math.BigDecimal;

public class InventoryStockDto {

    private Long id;
    private Long branchId;
    private String branchName;
    private Long sparePartId;
    private String partName;
    private String partCode;
    private String categoryName;
    private Integer quantity;
    private Integer minimumStockAlert;
    private Boolean isLowStock;
    private BigDecimal unitCost;

    public InventoryStockDto() {}

    public InventoryStockDto(Long id, Long branchId, String branchName, Long sparePartId,
                             String partName, String partCode, String categoryName,
                             Integer quantity, Integer minimumStockAlert,
                             Boolean isLowStock, BigDecimal unitCost) {
        this.id = id;
        this.branchId = branchId;
        this.branchName = branchName;
        this.sparePartId = sparePartId;
        this.partName = partName;
        this.partCode = partCode;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.minimumStockAlert = minimumStockAlert;
        this.isLowStock = isLowStock;
        this.unitCost = unitCost;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public Long getSparePartId() { return sparePartId; }
    public void setSparePartId(Long sparePartId) { this.sparePartId = sparePartId; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getMinimumStockAlert() { return minimumStockAlert; }
    public void setMinimumStockAlert(Integer minimumStockAlert) { this.minimumStockAlert = minimumStockAlert; }

    public Boolean getIsLowStock() { return isLowStock; }
    public void setIsLowStock(Boolean isLowStock) { this.isLowStock = isLowStock; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
}
