package com.techfix.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_bookings")
public class BookingEntity {

    @PrimaryKey
    private Long id;
    private String bookingReference;
    private String serviceName;
    private String branchName;
    private String deviceBrand;
    private String deviceModel;
    private String problemDescription;
    private String currentStatus;
    private String statusDisplayName;
    private double totalCost;
    private String appointmentDate;
    private String createdAt;

    public BookingEntity(Long id, String bookingReference, String serviceName, String branchName,
                         String deviceBrand, String deviceModel, String problemDescription,
                         String currentStatus, String statusDisplayName, double totalCost,
                         String appointmentDate, String createdAt) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.serviceName = serviceName;
        this.branchName = branchName;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.currentStatus = currentStatus;
        this.statusDisplayName = statusDisplayName;
        this.totalCost = totalCost;
        this.appointmentDate = appointmentDate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getDeviceBrand() { return deviceBrand; }
    public void setDeviceBrand(String deviceBrand) { this.deviceBrand = deviceBrand; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
