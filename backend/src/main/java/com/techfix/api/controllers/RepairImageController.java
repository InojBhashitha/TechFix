package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.dto.RepairImageDto;
import com.techfix.api.services.RepairImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class RepairImageController {

    private final RepairImageService repairImageService;

    public RepairImageController(RepairImageService repairImageService) {
        this.repairImageService = repairImageService;
    }

    @PostMapping("/upload-repair-image")
    public ResponseEntity<ApiResponse<RepairImageDto>> uploadRepairImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookingReference") String bookingReference,
            @RequestParam(value = "imageType", required = false) String imageType,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            RepairImageDto response = repairImageService.uploadRepairImage(
                    file, bookingReference, imageType, userDetails.getUsername()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Device/repair image uploaded successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
