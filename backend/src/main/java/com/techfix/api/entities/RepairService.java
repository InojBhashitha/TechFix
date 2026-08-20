package com.techfix.api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "repair_services")
public class RepairService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private DeviceCategory category;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    private Integer estimatedDurationMinutes = 60;

    private String sampleImageUrl;

    private Boolean isActive = true;

    public RepairService() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DeviceCategory getCategory() { return category; }
    public void setCategory(DeviceCategory category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public String getSampleImageUrl() { return sampleImageUrl; }
    public void setSampleImageUrl(String sampleImageUrl) { this.sampleImageUrl = sampleImageUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
