package com.techfix.api.services;

import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.dto.RepairTrackingDto;
import com.techfix.api.entities.*;
import com.techfix.api.enums.RepairStatus;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RepairTrackingServiceTest {

    @Mock
    private RepairRequestRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RepairStatusHistoryRepository statusHistoryRepository;

    @InjectMocks
    private BookingService bookingService;

    private User customer;
    private User otherCustomer;
    private User staffColombo;
    private User staffGalle;
    private User admin;

    private Branch colomboBranch;

    private RepairRequest repairRequest;
    private List<RepairStatusHistory> historyList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup users
        customer = new User("Kamal Perera", "customer@techfix.lk", "0771234567", "hash", UserRole.CUSTOMER);
        customer.setId(101L);

        otherCustomer = new User("Nimal Silva", "other@techfix.lk", "0777654321", "hash", UserRole.CUSTOMER);
        otherCustomer.setId(102L);

        staffColombo = new User("Colombo Staff", "staff.colombo@techfix.lk", "0711111111", "hash", UserRole.STAFF);
        staffColombo.setId(103L);
        staffColombo.setBranchId(1L);

        staffGalle = new User("Galle Staff", "staff.galle@techfix.lk", "0712222222", "hash", UserRole.STAFF);
        staffGalle.setId(104L);
        staffGalle.setBranchId(2L);

        admin = new User("TechFix Admin", "admin@techfix.lk", "0713333333", "hash", UserRole.ADMIN);
        admin.setId(105L);

        // Setup branches
        colomboBranch = new Branch(1L, "Colombo Branch", "Addr 1", "phone1", 6.9, 79.8, true);

        // Setup repair request
        repairRequest = new RepairRequest();
        repairRequest.setId(501L);
        repairRequest.setBookingReference("TF-2026-TEST");
        repairRequest.setCustomer(customer);
        repairRequest.setBranch(colomboBranch);
        
        RepairService repairService = new RepairService();
        repairService.setId(10L);
        repairService.setName("Screen Replacement");
        DeviceCategory category = new DeviceCategory();
        category.setName("Mobile Phone");
        repairService.setCategory(category);
        repairRequest.setService(repairService);

        repairRequest.setDeviceBrand("Apple");
        repairRequest.setDeviceModel("iPhone 13");
        repairRequest.setProblemDescription("Screen cracked");
        repairRequest.setCurrentStatus(RepairStatus.REQUEST_SUBMITTED);

        // Setup status history
        historyList = new ArrayList<>();
        historyList.add(new RepairStatusHistory(repairRequest, RepairStatus.REQUEST_SUBMITTED, "Created", customer));
        historyList.get(0).setTimestamp(LocalDateTime.now().minusHours(2));

        historyList.add(new RepairStatusHistory(repairRequest, RepairStatus.DIAGNOSIS, "Inspecting faults", staffColombo));
        historyList.get(1).setTimestamp(LocalDateTime.now().minusHours(1));
    }

    @Test
    void testGetBookingTrackingCustomerSuccess() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("customer@techfix.lk")).thenReturn(Optional.of(customer));
        when(statusHistoryRepository.findByRepairRequestIdOrderByTimestampAsc(501L)).thenReturn(historyList);

        RepairTrackingDto tracking = bookingService.getBookingTracking("TF-2026-TEST", "customer@techfix.lk");

        assertNotNull(tracking);
        assertEquals("TF-2026-TEST", tracking.getBookingReference());
        assertEquals(2, tracking.getHistory().size());
        assertEquals("Inspecting faults", tracking.getHistory().get(1).getNotes());
        assertEquals("Colombo Staff", tracking.getHistory().get(1).getUpdatedByName());
    }

    @Test
    void testGetBookingTrackingCustomerAccessDenied() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("other@techfix.lk")).thenReturn(Optional.of(otherCustomer));

        Exception exception = assertThrows(RuntimeException.class, () ->
                bookingService.getBookingTracking("TF-2026-TEST", "other@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Access Denied: Customers can only view tracking"));
    }

    @Test
    void testGetBookingTrackingStaffSuccess() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffColombo));
        when(statusHistoryRepository.findByRepairRequestIdOrderByTimestampAsc(501L)).thenReturn(historyList);

        RepairTrackingDto tracking = bookingService.getBookingTracking("TF-2026-TEST", "staff.colombo@techfix.lk");

        assertNotNull(tracking);
        assertEquals("TF-2026-TEST", tracking.getBookingReference());
    }

    @Test
    void testGetBookingTrackingStaffAccessDenied() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("staff.galle@techfix.lk")).thenReturn(Optional.of(staffGalle));

        Exception exception = assertThrows(RuntimeException.class, () ->
                bookingService.getBookingTracking("TF-2026-TEST", "staff.galle@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Access Denied: Technicians can only view tracking"));
    }

    @Test
    void testGetBookingTrackingAdminSuccess() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("admin@techfix.lk")).thenReturn(Optional.of(admin));
        when(statusHistoryRepository.findByRepairRequestIdOrderByTimestampAsc(501L)).thenReturn(historyList);

        RepairTrackingDto tracking = bookingService.getBookingTracking("TF-2026-TEST", "admin@techfix.lk");

        assertNotNull(tracking);
        assertEquals("TF-2026-TEST", tracking.getBookingReference());
    }

    @Test
    void testUpdateBookingStatusSuccess() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffColombo));
        when(bookingRepository.save(any(RepairRequest.class))).thenReturn(repairRequest);

        BookingResponseDto response = bookingService.updateBookingStatus(
                "TF-2026-TEST", RepairStatus.REPAIRING, "Soldering active", "staff.colombo@techfix.lk"
        );

        assertNotNull(response);
        verify(statusHistoryRepository, times(1)).save(any(RepairStatusHistory.class));
        assertEquals(RepairStatus.REPAIRING, repairRequest.getCurrentStatus());
    }

    @Test
    void testUpdateBookingStatusUnchanged() {
        when(bookingRepository.findByBookingReference("TF-2026-TEST")).thenReturn(Optional.of(repairRequest));
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffColombo));

        BookingResponseDto response = bookingService.updateBookingStatus(
                "TF-2026-TEST", RepairStatus.REQUEST_SUBMITTED, "Inspecting again", "staff.colombo@techfix.lk"
        );

        assertNotNull(response);
        // Should bypass repository save since status was already REQUEST_SUBMITTED
        verify(statusHistoryRepository, never()).save(any(RepairStatusHistory.class));
    }

    @Test
    void testBookingNotFound() {
        when(bookingRepository.findByBookingReference("TF-UNKNOWN")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                bookingService.getBookingTracking("TF-UNKNOWN", "customer@techfix.lk")
        );

        assertTrue(exception.getMessage().contains("Booking not found"));
    }
}
