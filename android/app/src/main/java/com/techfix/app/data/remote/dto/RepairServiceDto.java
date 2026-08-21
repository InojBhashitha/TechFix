package com.techfix.app.data.remote.dto;

import java.math.BigDecimal;

public class RepairServiceDto {
    private Long id;
    private DeviceCategoryDto category;
    private String name;
    private String description;
    private BigDecimal estimatedPrice;
    private Integer estimatedDurationMinutes;
    private String sampleImageUrl;
    private Boolean isActive;

    public Long getId() { return id; }
    public DeviceCategoryDto getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public String getSampleImageUrl() { return sampleImageUrl; }
    public Boolean getIsActive() { return isActive; }
}
