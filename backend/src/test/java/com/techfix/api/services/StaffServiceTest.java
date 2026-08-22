package com.techfix.api.services;

import com.techfix.api.dto.AssignTechnicianRequestDto;
import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.dto.InventoryStockDto;
import com.techfix.api.dto.StaffDashboardStatsDto;
import com.techfix.api.dto.TechnicianDto;
import com.techfix.api.dto.UpdateInventoryStockRequestDto;
import com.techfix.api.dto.UpdateRepairStatusRequestDto;
import com.techfix.api.entities.Branch;
import com.techfix.api.entities.BranchInventory;
import com.techfix.api.entities.RepairRequest;
import com.techfix.api.entities.RepairService;
import com.techfix.api.entities.RepairStatusHistory;
import com.techfix.api.entities.SparePart;
import com.techfix.api.entities.Technician;
import com.techfix.api.entities.User;
import com.techfix.api.enums.RepairStatus;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.BranchInventoryRepository;
import com.techfix.api.repositories.RepairRequestRepository;
import com.techfix.api.repositories.RepairStatusHistoryRepository;
import com.techfix.api.repositories.TechnicianRepository;
import com.techfix.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StaffServiceTest {

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private RepairStatusHistoryRepository statusHistoryRepository;

    @Mock
    private BranchInventoryRepository branchInventoryRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StaffService staffService;

    private User staffUserColombo;
    private User staffUserGalle;
    private Branch colomboBranch;
    private Branch galleBranch;
    private RepairRequest sampleRepair;
    private Technician colomboTech;
    private Technician galleTech;
    private BranchInventory sampleInventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        colomboBranch = new Branch(1L, "TechFix Colombo", "Colombo Road", "0112345678", 6.9271, 79.8612, true);
        galleBranch = new Branch(2L, "TechFix Galle", "Galle Road", "0912234567", 6.0535, 80.2210, true);

        staffUserColombo = new User("Staff Colombo", "staff.colombo@techfix.lk", "0771234567", "password", UserRole.STAFF);
        staffUserColombo.setId(10L);
        staffUserColombo.setBranchId(1L);

        staffUserGalle = new User("Staff Galle", "staff.galle@techfix.lk", "0777654321", "password", UserRole.STAFF);
        staffUserGalle.setId(11L);
        staffUserGalle.setBranchId(2L);

        colomboTech = new Technician();
        colomboTech.setId(100L);
        colomboTech.setFullName("Kavindu Perera");
        colomboTech.setBranch(colomboBranch);
        colomboTech.setSpecialization("Mobile Hardware");
        colomboTech.setIsAvailable(true);
        colomboTech.setActiveRepairsCount(1);

        galleTech = new Technician();
        galleTech.setId(200L);
        galleTech.setFullName("Ruwan Silva");
        galleTech.setBranch(galleBranch);
        galleTech.setSpecialization("Laptop Repairs");
        galleTech.setIsAvailable(true);
        galleTech.setActiveRepairsCount(0);

        RepairService screenRepair = new RepairService();
        screenRepair.setId(1L);
        screenRepair.setName("Screen Replacement");
        screenRepair.setEstimatedPrice(BigDecimal.valueOf(15000));

        sampleRepair = new RepairRequest();
        sampleRepair.setId(500L);
        sampleRepair.setBookingReference("TF-2026-9999");
        sampleRepair.setCustomer(staffUserColombo);
        sampleRepair.setBranch(colomboBranch);
        sampleRepair.setService(screenRepair);
        sampleRepair.setDeviceBrand("Apple");
        sampleRepair.setDeviceModel("iPhone 13");
        sampleRepair.setProblemDescription("Cracked Screen");
        sampleRepair.setCurrentStatus(RepairStatus.REQUEST_SUBMITTED);
        sampleRepair.setTotalCost(BigDecimal.valueOf(15000));

        SparePart part = new SparePart();
        part.setId(10L);
        part.setName("iPhone 13 OLED Display");
        part.setPartCode("DISP-IP13");
        part.setUnitCost(BigDecimal.valueOf(12000));

        sampleInventory = new BranchInventory();
        sampleInventory.setId(1L);
        sampleInventory.setBranch(colomboBranch);
        sampleInventory.setSparePart(part);
        sampleInventory.setQuantity(1);
        sampleInventory.setMinimumStockAlert(2);
    }

    @Test
    void testGetDashboardStats_StaffRole() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(repairRequestRepository.countByBranchId(1L)).thenReturn(10L);
        when(repairRequestRepository.countByBranchIdAndCurrentStatusIn(eq(1L), anyList())).thenReturn(3L);
        when(branchInventoryRepository.findByBranchId(1L)).thenReturn(List.of(sampleInventory));
        when(technicianRepository.countByBranchIdAndIsAvailableTrue(1L)).thenReturn(2L);

        StaffDashboardStatsDto stats = staffService.getDashboardStats("staff.colombo@techfix.lk", null);

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalRepairs());
        assertEquals(1L, stats.getLowStockAlerts());
        assertEquals(2L, stats.getAvailableTechnicians());
    }

    @Test
    void testUpdateRepairStatus_ValidTransition() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(repairRequestRepository.findById(500L)).thenReturn(Optional.of(sampleRepair));
        when(repairRequestRepository.save(any(RepairRequest.class))).thenAnswer(i -> i.getArgument(0));

        UpdateRepairStatusRequestDto request = new UpdateRepairStatusRequestDto(RepairStatus.DEVICE_RECEIVED, "Device received at Colombo counter", null);

        BookingResponseDto result = staffService.updateRepairStatus("500", request, "staff.colombo@techfix.lk");

        assertNotNull(result);
        assertEquals(RepairStatus.DEVICE_RECEIVED, result.getCurrentStatus());
        verify(statusHistoryRepository, times(1)).save(any(RepairStatusHistory.class));
    }

    @Test
    void testUpdateRepairStatus_InvalidBackwardTransition() {
        sampleRepair.setCurrentStatus(RepairStatus.REPAIRING);
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(repairRequestRepository.findById(500L)).thenReturn(Optional.of(sampleRepair));

        UpdateRepairStatusRequestDto request = new UpdateRepairStatusRequestDto(RepairStatus.DIAGNOSIS, "Back to diagnosis", null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            staffService.updateRepairStatus("500", request, "staff.colombo@techfix.lk");
        });

        assertTrue(exception.getMessage().contains("Status must progress forward"));
    }

    @Test
    void testUpdateRepairStatus_CompletedStatusLocked() {
        sampleRepair.setCurrentStatus(RepairStatus.COMPLETED);
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(repairRequestRepository.findById(500L)).thenReturn(Optional.of(sampleRepair));

        UpdateRepairStatusRequestDto request = new UpdateRepairStatusRequestDto(RepairStatus.REPAIRING, "Try to reopen", null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            staffService.updateRepairStatus("500", request, "staff.colombo@techfix.lk");
        });

        assertTrue(exception.getMessage().contains("already COMPLETED"));
    }

    @Test
    void testAssignTechnician_Success() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(repairRequestRepository.findById(500L)).thenReturn(Optional.of(sampleRepair));
        when(technicianRepository.findById(100L)).thenReturn(Optional.of(colomboTech));
        when(repairRequestRepository.save(any(RepairRequest.class))).thenAnswer(i -> i.getArgument(0));

        AssignTechnicianRequestDto request = new AssignTechnicianRequestDto(100L, "Assigned to Kavindu");

        BookingResponseDto response = staffService.assignTechnician("500", request, "staff.colombo@techfix.lk");

        assertNotNull(response);
        assertEquals(100L, response.getTechnicianId());
        assertEquals("Kavindu Perera", response.getTechnicianName());
        assertEquals(RepairStatus.BRANCH_ASSIGNED, response.getCurrentStatus());
    }

    @Test
    void testAssignTechnician_WrongBranch() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(repairRequestRepository.findById(500L)).thenReturn(Optional.of(sampleRepair));
        when(technicianRepository.findById(200L)).thenReturn(Optional.of(galleTech)); // Galle tech for Colombo repair

        AssignTechnicianRequestDto request = new AssignTechnicianRequestDto(200L, "Assign Galle tech");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            staffService.assignTechnician("500", request, "staff.colombo@techfix.lk");
        });

        assertTrue(exception.getMessage().contains("Technician does not belong to branch"));
    }

    @Test
    void testGetTechnicians_FilteredByBranch() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(technicianRepository.findByBranchId(1L)).thenReturn(List.of(colomboTech));

        List<TechnicianDto> result = staffService.getTechnicians("staff.colombo@techfix.lk", null, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Kavindu Perera", result.get(0).getFullName());
    }

    @Test
    void testGetInventory_LowStockFilter() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(branchInventoryRepository.findByBranchId(1L)).thenReturn(List.of(sampleInventory));

        List<InventoryStockDto> result = staffService.getInventory("staff.colombo@techfix.lk", null, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsLowStock());
    }

    @Test
    void testUpdateInventoryStock_Success() {
        when(userRepository.findByEmail("staff.colombo@techfix.lk")).thenReturn(Optional.of(staffUserColombo));
        when(branchInventoryRepository.findById(1L)).thenReturn(Optional.of(sampleInventory));
        when(branchInventoryRepository.save(any(BranchInventory.class))).thenAnswer(i -> i.getArgument(0));

        UpdateInventoryStockRequestDto request = new UpdateInventoryStockRequestDto(15, 5);

        InventoryStockDto updated = staffService.updateInventoryStock(1L, request, "staff.colombo@techfix.lk");

        assertNotNull(updated);
        assertEquals(15, updated.getQuantity());
        assertEquals(5, updated.getMinimumStockAlert());
        assertFalse(updated.getIsLowStock());
    }
}
