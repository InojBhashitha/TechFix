package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_categories")
public class CategoryEntity {

    @PrimaryKey
    private Long id;
    private String name;
    private String iconName;
    private String description;

    public CategoryEntity(Long id, String name, String iconName, String description) {
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
