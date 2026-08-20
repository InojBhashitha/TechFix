package com.techfix.api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "spare_parts")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String partCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compatible_category_id")
    private DeviceCategory compatibleCategory;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitCost;

    public SparePart() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }

    public DeviceCategory getCompatibleCategory() { return compatibleCategory; }
    public void setCompatibleCategory(DeviceCategory compatibleCategory) { this.compatibleCategory = compatibleCategory; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
}
