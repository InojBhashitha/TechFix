package com.techfix.app.data.remote.dto;

public class BookingRequestDto {
    private Long serviceId;
    private Long branchId;
    private String deviceBrand;
    private String deviceModel;
    private String serialNumber;
    private String problemDescription;
    private String appointmentDate;
    private Double customerLatitude;
    private Double customerLongitude;

    public BookingRequestDto(Long serviceId, Long branchId, String deviceBrand, String deviceModel,
                             String serialNumber, String problemDescription, String appointmentDate) {
        this.serviceId = serviceId;
        this.branchId = branchId;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.serialNumber = serialNumber;
        this.problemDescription = problemDescription;
        this.appointmentDate = appointmentDate;
    }

    public Long getServiceId() { return serviceId; }
    public Long getBranchId() { return branchId; }
    public String getDeviceBrand() { return deviceBrand; }
    public String getDeviceModel() { return deviceModel; }
    public String getSerialNumber() { return serialNumber; }
    public String getProblemDescription() { return problemDescription; }
    public String getAppointmentDate() { return appointmentDate; }
    public Double getCustomerLatitude() { return customerLatitude; }
    public Double getCustomerLongitude() { return customerLongitude; }

    public void setCustomerLatitude(Double customerLatitude) { this.customerLatitude = customerLatitude; }
    public void setCustomerLongitude(Double customerLongitude) { this.customerLongitude = customerLongitude; }
}
