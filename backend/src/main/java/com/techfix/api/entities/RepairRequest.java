package com.techfix.api.entities;

import com.techfix.api.enums.RepairStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_requests")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private RepairService service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(nullable = false)
    private String deviceBrand;

    @Column(nullable = false)
    private String deviceModel;

    private String serialNumber;

    @Column(columnDefinition = "TEXT")
    private String problemDescription;

    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairStatus currentStatus = RepairStatus.REQUEST_SUBMITTED;

    private Double customerLatitude;
    private Double customerLongitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "repairRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepairImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "repairRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepairStatusHistory> statusHistory = new ArrayList<>();

    @OneToOne(mappedBy = "repairRequest", cascade = CascadeType.ALL)
    private Payment payment;

    public RepairRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public RepairService getService() { return service; }
    public void setService(RepairService service) { this.service = service; }

    public Technician getTechnician() { return technician; }
    public void setTechnician(Technician technician) { this.technician = technician; }

    public String getDeviceBrand() { return deviceBrand; }
    public void setDeviceBrand(String deviceBrand) { this.deviceBrand = deviceBrand; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public RepairStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(RepairStatus currentStatus) { this.currentStatus = currentStatus; }

    public Double getCustomerLatitude() { return customerLatitude; }
    public void setCustomerLatitude(Double customerLatitude) { this.customerLatitude = customerLatitude; }

    public Double getCustomerLongitude() { return customerLongitude; }
    public void setCustomerLongitude(Double customerLongitude) { this.customerLongitude = customerLongitude; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<RepairImage> getImages() { return images; }
    public void setImages(List<RepairImage> images) { this.images = images; }

    public List<RepairStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<RepairStatusHistory> statusHistory) { this.statusHistory = statusHistory; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}
