package com.techfix.api.dto;

import com.techfix.api.entities.Branch;

public class BranchDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;

    public BranchDto() {}

    public BranchDto(Branch branch) {
        if (branch != null) {
            this.id = branch.getId();
            this.name = branch.getName();
            this.address = branch.getAddress();
            this.phone = branch.getPhone();
            this.latitude = branch.getLatitude();
            this.longitude = branch.getLongitude();
            this.isActive = branch.getIsActive();
        }
    }

    public BranchDto(Long id, String name, String address, String phone, Double latitude, Double longitude, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
