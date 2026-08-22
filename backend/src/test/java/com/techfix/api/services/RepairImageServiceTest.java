package com.techfix.api.services;

import com.techfix.api.dto.RepairImageDto;
import com.techfix.api.entities.*;
import com.techfix.api.enums.ImageType;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.RepairImageRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import com.techfix.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RepairImageServiceTest {

    @Mock
    private RepairImageRepository repairImageRepository;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RepairImageService repairImageService;

    @TempDir
    Path tempDir;

    private User customer;
    private User otherCustomer;
    private User staffColombo;
    private User staffGalle;
    
    private Branch colomboBranch;
    
    private RepairRequest repairRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Inject temp directory into service upload directory config
        ReflectionTestUtils.setField(repairImageService, "uploadDir", tempDir.toString());

        // Setup users
        customer = new User("Kamal Perera", "kamal@techfix.lk", "0771234567", "hash", UserRole.CUSTOMER);
        customer.setId(1L);

        otherCustomer = new User("Nimal Silva", "nimal@techfix.lk", "0777654321", "hash", UserRole.CUSTOMER);
        otherCustomer.setId(2L);

        staffColombo = new User("Colombo Staff", "staff.colombo@techfix.lk", "0711111111", "hash", UserRole.STAFF);
        staffColombo.setId(3L);
        staffColombo.setBranchId(1L);

        staffGalle = new User("Galle Staff", "staff.galle@techfix.lk", "0712222222", "hash", UserRole.STAFF);
        staffGalle.setId(4L);
        staffGalle.setBranchId(2L);

        // Setup branches
        colomboBranch = new Branch(1L, "Colombo Branch", "Addr 1", "phone1", 6.9, 79.8, true);

        // Setup repair request
        repairRequest = new RepairRequest();
        repairRequest.setId(100L);
        repairRequest.setBookingReference("TF-2026-TEST");
        repairRequest.setCustomer(customer);
        repairRequest.setBranch(colomboBranch);
    }

    /**
     * Test 1: Successful upload by owner customer.
     */
    @Test
    void testSuccessfulUploadByCustomerOwner() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "device.jpg", "image/jpeg", "fake image content".getBytes()
        );

        when(repairRequestRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("kamal@techfix.lk")).thenReturn(Optional.of(customer));
        
        RepairImage mockedSavedImage = new RepairImage(repairRequest, "/uploads/repairs/saved.jpg", ImageType.CUSTOMER_DAMAGE);
        mockedSavedImage.setId(10L);
        when(repairImageRepository.save(any(RepairImage.class))).thenReturn(mockedSavedImage);

        RepairImageDto result = repairImageService.uploadRepairImage(
                file, "TF-2026-TEST", "CUSTOMER_DAMAGE", "kamal@techfix.lk"
        );

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("TF-2026-TEST", result.getBookingReference());
        assertEquals("/uploads/repairs/saved.jpg", result.getImageUrl());
        assertEquals("CUSTOMER_DAMAGE", result.getImageType());
    }

    /**
     * Test 2: Upload fails if file is empty.
     */
    @Test
    void testUploadEmptyFileThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "", "image/jpeg", new byte[0]
        );

        Exception exception = assertThrows(RuntimeException.class, () ->
                repairImageService.uploadRepairImage(file, "TF-2026-TEST", "CUSTOMER_DAMAGE", "kamal@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("File cannot be empty"));
    }

    /**
     * Test 3: Upload fails with unsupported file type (e.g. text/plain).
     */
    @Test
    void testUploadUnsupportedTypeThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "script.sh", "text/plain", "echo hello".getBytes()
        );

        Exception exception = assertThrows(RuntimeException.class, () ->
                repairImageService.uploadRepairImage(file, "TF-2026-TEST", "CUSTOMER_DAMAGE", "kamal@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Unsupported file type"));
    }

    /**
     * Test 4: Upload fails with invalid booking reference.
     */
    @Test
    void testUploadInvalidBookingReferenceThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "device.jpg", "image/jpeg", "fake image content".getBytes()
        );

        when(repairRequestRepository.findByBookingReference("INVALID-REF")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                repairImageService.uploadRepairImage(file, "INVALID-REF", "CUSTOMER_DAMAGE", "kamal@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Repair booking not found"));
    }

    /**
     * Test 5: Upload fails for unauthorized customer (non-owner).
     */
    @Test
    void testUploadUnauthorizedCustomerThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "device.jpg", "image/jpeg", "fake image content".getBytes()
        );

        when(repairRequestRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("nimal@techfix.lk")).thenReturn(Optional.of(otherCustomer));

        Exception exception = assertThrows(RuntimeException.class, () ->
                repairImageService.uploadRepairImage(file, "TF-2026-TEST", "CUSTOMER_DAMAGE", "nimal@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Access Denied: Customers can only upload images"));
    }

    /**
     * Test 6: Upload succeeds for staff member matching repair branch, fails for other branch staff.
     */
    @Test
    void testUploadStaffAuthorization() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "device.jpg", "image/jpeg", "fake image content".getBytes()
        );

        when(repairRequestRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        
        // Setup mock save behavior
        RepairImage colomboImage = new RepairImage(repairRequest, "/uploads/repairs/colombo.jpg", ImageType.STAFF_PRE_REPAIR);
        colomboImage.setId(11L);
        when(repairImageRepository.save(any(RepairImage.class))).thenReturn(colomboImage);

        // Colombo staff member (assigned to branch 1, repair branch is 1) -> Success
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffColombo));
        
        RepairImageDto result = repairImageService.uploadRepairImage(
                file, "TF-2026-TEST", "STAFF_PRE_REPAIR", "staff.colombo@techfix.lk"
        );
        
        assertNotNull(result);
        assertEquals(11L, result.getId());

        // Galle staff member (assigned to branch 2, repair branch is 1) -> Fails
        when(userRepository.findByEmail("staff.galle@techfix.lk")).thenReturn(Optional.of(staffGalle));
        
        Exception exception = assertThrows(RuntimeException.class, () ->
                repairImageService.uploadRepairImage(file, "TF-2026-TEST", "STAFF_PRE_REPAIR", "staff.galle@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Access Denied: Technicians can only upload images for repair requests assigned to their branch."));
    }
}
