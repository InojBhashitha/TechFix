package com.techfix.api.dto;

import com.techfix.api.entities.RepairImage;
import java.time.LocalDateTime;

public class RepairImageDto {
    private Long id;
    private String bookingReference;
    private String imageUrl;
    private String imageType;
    private LocalDateTime uploadedAt;

    public RepairImageDto() {}

    public RepairImageDto(RepairImage image) {
        this.id = image.getId();
        this.bookingReference = image.getRepairRequest().getBookingReference();
        this.imageUrl = image.getImageUrl();
        this.imageType = image.getImageType().name();
        this.uploadedAt = image.getUploadedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
