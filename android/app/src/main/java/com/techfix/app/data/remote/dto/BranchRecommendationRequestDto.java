package com.techfix.app.data.remote.dto;

public class BranchRecommendationRequestDto {
    private Double latitude;
    private Double longitude;
    private Long serviceId;
    private String deviceBrand;
    private String deviceModel;

    public BranchRecommendationRequestDto() {}

    public BranchRecommendationRequestDto(Double latitude, Double longitude, Long serviceId, String deviceBrand, String deviceModel) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.serviceId = serviceId;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
    }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getDeviceBrand() { return deviceBrand; }
    public void setDeviceBrand(String deviceBrand) { this.deviceBrand = deviceBrand; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }
}
