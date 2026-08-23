package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.math.BigDecimal;

@Entity(tableName = "cached_services")
public class ServiceEntity {

    @PrimaryKey
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private double estimatedPrice;
    private Integer estimatedDurationMinutes;
    private String sampleImageUrl;

    public ServiceEntity(Long id, Long categoryId, String categoryName, String name,
                         String description, double estimatedPrice,
                         Integer estimatedDurationMinutes, String sampleImageUrl) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.estimatedPrice = estimatedPrice;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.sampleImageUrl = sampleImageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(double estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public String getSampleImageUrl() { return sampleImageUrl; }
    public void setSampleImageUrl(String sampleImageUrl) { this.sampleImageUrl = sampleImageUrl; }
}
