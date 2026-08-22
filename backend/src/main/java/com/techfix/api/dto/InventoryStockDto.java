package com.techfix.api.dto;

public class InventoryStockDto {
    private Long id;
    private Long branchId;
    private String branchName;
    private Long sparePartId;
    private String partName;
    private String partCode;
    private Integer quantity;
    private Integer reorderLevel;
    private Boolean isLowStock;
    private Double unitPrice;

    public InventoryStockDto() {}

    public InventoryStockDto(Long id, Long branchId, String branchName, Long sparePartId, String partName, String partCode, Integer quantity, Integer reorderLevel, Boolean isLowStock, Double unitPrice) {
        this.id = id;
        this.branchId = branchId;
        this.branchName = branchName;
        this.sparePartId = sparePartId;
        this.partName = partName;
        this.partCode = partCode;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.isLowStock = isLowStock;
        this.unitPrice = unitPrice;
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

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public Boolean getIsLowStock() { return isLowStock; }
    public void setIsLowStock(Boolean lowStock) { isLowStock = lowStock; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}
