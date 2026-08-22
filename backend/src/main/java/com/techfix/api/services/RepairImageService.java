package com.techfix.api.services;

import com.techfix.api.dto.RepairImageDto;
import com.techfix.api.entities.RepairImage;
import com.techfix.api.entities.RepairRequest;
import com.techfix.api.entities.User;
import com.techfix.api.enums.ImageType;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.RepairImageRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import com.techfix.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class RepairImageService {

    private final RepairImageRepository repairImageRepository;
    private final RepairRequestRepository repairRequestRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/repairs}")
    private String uploadDir;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public RepairImageService(RepairImageRepository repairImageRepository,
                              RepairRequestRepository repairRequestRepository,
                              UserRepository userRepository) {
        this.repairImageRepository = repairImageRepository;
        this.repairRequestRepository = repairRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RepairImageDto uploadRepairImage(MultipartFile file, String bookingReference, String imageTypeStr, String currentUserEmail) {
        // 1. Validation
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File cannot be empty or missing");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Unsupported file type. Only JPEG, PNG, GIF and WEBP images are allowed.");
        }

        // 2. Fetch repair request
        RepairRequest repairRequest = repairRequestRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("Repair booking not found with reference: " + bookingReference));

        // 3. Authorization Checks
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Logged in user not found: " + currentUserEmail));

        if (currentUser.getRole() == UserRole.CUSTOMER) {
            if (!repairRequest.getCustomer().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access Denied: Customers can only upload images to their own repair requests.");
            }
        } else if (currentUser.getRole() == UserRole.STAFF) {
            // Staff member check
            if (currentUser.getBranchId() == null || !repairRequest.getBranch().getId().equals(currentUser.getBranchId())) {
                throw new RuntimeException("Access Denied: Technicians can only upload images for repair requests assigned to their branch.");
            }
        } else if (currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Access Denied: Unauthorized role");
        }

        // 4. Map ImageType enum
        ImageType imageType = ImageType.CUSTOMER_DAMAGE;
        if (imageTypeStr != null && !imageTypeStr.trim().isEmpty()) {
            try {
                imageType = ImageType.valueOf(imageTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid image category/type. Allowed values are: CUSTOMER_DAMAGE, STAFF_PRE_REPAIR, STAFF_POST_REPAIR.");
            }
        }

        // 5. Store File locally
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg"; // fallback
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Create a unique file name
            String uniqueFilename = bookingReference + "_" + UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 6. DB entity creation
            // imageUrl will look like: /uploads/repairs/uniqueFilename
            String relativeUrlPath = "/uploads/repairs/" + uniqueFilename;

            RepairImage repairImage = new RepairImage();
            repairImage.setRepairRequest(repairRequest);
            repairImage.setImageUrl(relativeUrlPath);
            repairImage.setImageType(imageType);

            RepairImage saved = repairImageRepository.save(repairImage);
            return new RepairImageDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Storage failure: Could not save the uploaded image file. " + e.getMessage());
        }
    }
}
