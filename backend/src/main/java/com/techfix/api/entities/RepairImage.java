package com.techfix.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techfix.api.enums.ImageType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_images")
public class RepairImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_request_id", nullable = false)
    private RepairRequest repairRequest;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType = ImageType.CUSTOMER_DAMAGE;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    public RepairImage() {}

    public RepairImage(RepairRequest repairRequest, String imageUrl, ImageType imageType) {
        this.repairRequest = repairRequest;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RepairRequest getRepairRequest() { return repairRequest; }
    public void setRepairRequest(RepairRequest repairRequest) { this.repairRequest = repairRequest; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ImageType getImageType() { return imageType; }
    public void setImageType(ImageType imageType) { this.imageType = imageType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
