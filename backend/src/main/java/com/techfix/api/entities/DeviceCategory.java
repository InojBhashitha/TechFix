package com.techfix.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "device_categories")
public class DeviceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String iconName;

    @Column(columnDefinition = "TEXT")
    private String description;

    public DeviceCategory() {}

    public DeviceCategory(Long id, String name, String iconName, String description) {
        this.id = id;
        this.name = name;
        this.iconName = iconName;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
