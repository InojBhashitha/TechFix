package com.techfix.api.services;

import com.techfix.api.dto.BranchRecommendationRequestDto;
import com.techfix.api.dto.BranchRecommendationResponseDto;
import com.techfix.api.entities.*;
import com.techfix.api.repositories.BranchInventoryRepository;
import com.techfix.api.repositories.BranchRepository;
import com.techfix.api.repositories.RepairServiceRepository;
import com.techfix.api.repositories.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchInventoryRepository inventoryRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private RepairServiceRepository serviceRepository;

    @InjectMocks
    private BranchService branchService;

    private Branch colombo;
    private Branch galle;
    private RepairService mobileScreenRepair;
    private RepairService desktopPsuRepair;
    private DeviceCategory mobileCategory;
    private DeviceCategory desktopCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup branches
        colombo = new Branch(1L, "TechFix Colombo", "Colombo Road", "0112345678", 6.9271, 79.8612, true);
        galle = new Branch(2L, "TechFix Galle", "Galle Road", "0912234567", 6.0535, 80.2210, true);

        // Setup Categories
        mobileCategory = new DeviceCategory();
        mobileCategory.setId(1L);
        mobileCategory.setName("Mobile Phone");

        desktopCategory = new DeviceCategory();
        desktopCategory.setId(3L);
        desktopCategory.setName("Desktop Computer");

        // Setup Repair Services
        mobileScreenRepair = new RepairService();
        mobileScreenRepair.setId(1L);
        mobileScreenRepair.setName("Screen Replacement");
        mobileScreenRepair.setCategory(mobileCategory);
        mobileScreenRepair.setEstimatedPrice(BigDecimal.valueOf(12500.00));

        desktopPsuRepair = new RepairService();
        desktopPsuRepair.setId(9L);
        desktopPsuRepair.setName("Power Supply (PSU) Replacement");
        desktopPsuRepair.setCategory(desktopCategory);
        desktopPsuRepair.setEstimatedPrice(BigDecimal.valueOf(8500.00));
    }

    /**
     * Case 1: Nearest branch has available technicians and parts.
     * Galle customer (6.0500, 80.2200) requests Mobile Screen repair.
     * Galle has available techs and parts.
     * Result: Galle recommended.
     */
    @Test
    void testNearestBranchHasTechAndParts() {
        when(branchRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(colombo, galle));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mobileScreenRepair));

        // Galle has mobile technician
        Technician galleTech = new Technician();
        galleTech.setBranch(galle);
        galleTech.setIsAvailable(true);
        galleTech.setSpecialization("Mobile Hardware");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(2L)).thenReturn(Collections.singletonList(galleTech));

        // Colombo has mobile technician
        Technician colomboTech = new Technician();
        colomboTech.setBranch(colombo);
        colomboTech.setIsAvailable(true);
        colomboTech.setSpecialization("Mobile Hardware Specialist");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(1L)).thenReturn(Collections.singletonList(colomboTech));

        // Both have stock for iPhone 13 Screen (Part ID 1)
        BranchInventory colomboInv = new BranchInventory();
        colomboInv.setQuantity(5);
        when(inventoryRepository.findByBranchIdAndSparePartId(1L, 1L)).thenReturn(Optional.of(colomboInv));

        BranchInventory galleInv = new BranchInventory();
        galleInv.setQuantity(3);
        when(inventoryRepository.findByBranchIdAndSparePartId(2L, 1L)).thenReturn(Optional.of(galleInv));

        // Request from Galle area (lat=6.0500, lon=80.2200)
        BranchRecommendationRequestDto request = new BranchRecommendationRequestDto(
                6.0500, 80.2200, 1L, "Apple", "iPhone 13"
        );

        BranchRecommendationResponseDto response = branchService.getBranchRecommendation(request);

        assertNotNull(response);
        assertEquals(2L, response.getRecommendedBranch().getId()); // Galle recommended
        assertTrue(response.getIsTechnicianAvailable());
        assertTrue(response.getIsPartAvailable());
        assertTrue(response.getReason().contains("Recommended based on shortest distance"));
    }

    /**
     * Case 2: Nearest branch lacks parts.
     * Galle customer requests iPhone screen replacement.
     * Galle has 0 stock of screen, Colombo has 5.
     * Result: Rerouted to Colombo despite Galle being closer.
     */
    @Test
    void testNearestBranchLacksParts() {
        when(branchRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(colombo, galle));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mobileScreenRepair));

        // Both have mobile technicians
        Technician galleTech = new Technician();
        galleTech.setSpecialization("Mobile Hardware");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(2L)).thenReturn(Collections.singletonList(galleTech));

        Technician colomboTech = new Technician();
        colomboTech.setSpecialization("Mobile Specialist");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(1L)).thenReturn(Collections.singletonList(colomboTech));

        // Colombo has stock (quantity=5), Galle has 0 stock
        BranchInventory colomboInv = new BranchInventory();
        colomboInv.setQuantity(5);
        when(inventoryRepository.findByBranchIdAndSparePartId(1L, 1L)).thenReturn(Optional.of(colomboInv));

        BranchInventory galleInv = new BranchInventory();
        galleInv.setQuantity(0); // Lacks parts
        when(inventoryRepository.findByBranchIdAndSparePartId(2L, 1L)).thenReturn(Optional.of(galleInv));

        // Request from Galle area
        BranchRecommendationRequestDto request = new BranchRecommendationRequestDto(
                6.0500, 80.2200, 1L, "Apple", "iPhone 13"
        );

        BranchRecommendationResponseDto response = branchService.getBranchRecommendation(request);

        assertNotNull(response);
        assertEquals(1L, response.getRecommendedBranch().getId()); // Colombo recommended
        assertTrue(response.getIsPartAvailable());
        assertTrue(response.getReason().contains("shortest distance") && response.getReason().contains("spare parts in stock"));
    }

    /**
     * Case 3: Nearest branch lacks technician capability.
     * Galle customer requests Desktop PSU Replacement (Service ID=9).
     * Galle has no desktop technician, Colombo has Dinesh Fernando (Desktop).
     * Result: Galle lacks technician capability, Colombo recommended.
     */
    @Test
    void testNearestBranchLacksTechnicianCapability() {
        when(branchRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(colombo, galle));
        when(serviceRepository.findById(9L)).thenReturn(Optional.of(desktopPsuRepair));

        // Galle only has a laptop tech available
        Technician galleLaptopTech = new Technician();
        galleLaptopTech.setBranch(galle);
        galleLaptopTech.setSpecialization("Laptop Screens");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(2L)).thenReturn(Collections.singletonList(galleLaptopTech));

        // Colombo has a desktop tech available
        Technician colomboDesktopTech = new Technician();
        colomboDesktopTech.setBranch(colombo);
        colomboDesktopTech.setSpecialization("Desktop PCs and PSU Diagnostics");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(1L)).thenReturn(Collections.singletonList(colomboDesktopTech));

        // Both have stock for desktop PSU (Part ID 6)
        BranchInventory colomboInv = new BranchInventory();
        colomboInv.setQuantity(5);
        when(inventoryRepository.findByBranchIdAndSparePartId(1L, 6L)).thenReturn(Optional.of(colomboInv));

        BranchInventory galleInv = new BranchInventory();
        galleInv.setQuantity(4);
        when(inventoryRepository.findByBranchIdAndSparePartId(2L, 6L)).thenReturn(Optional.of(galleInv));

        // Request from Galle area
        BranchRecommendationRequestDto request = new BranchRecommendationRequestDto(
                6.0500, 80.2200, 9L, "Custom", "Gaming PC"
        );

        BranchRecommendationResponseDto response = branchService.getBranchRecommendation(request);

        assertNotNull(response);
        assertEquals(1L, response.getRecommendedBranch().getId()); // Colombo recommended due to tech capability
        assertTrue(response.getIsTechnicianAvailable());
        assertTrue(response.getIsPartAvailable());
    }

    /**
     * Case 4: Both branches are unavailable (e.g. parts are out of stock everywhere).
     * Result: Fallback to closest branch overall with a warning message.
     */
    @Test
    void testBothBranchesUnavailable() {
        when(branchRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(colombo, galle));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mobileScreenRepair));

        // Both have mobile technicians
        Technician galleTech = new Technician();
        galleTech.setSpecialization("Mobile Hardware");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(2L)).thenReturn(Collections.singletonList(galleTech));

        Technician colomboTech = new Technician();
        colomboTech.setSpecialization("Mobile Specialist");
        when(technicianRepository.findByBranchIdAndIsAvailableTrue(1L)).thenReturn(Collections.singletonList(colomboTech));

        // Both have 0 stock
        BranchInventory colomboInv = new BranchInventory();
        colomboInv.setQuantity(0);
        when(inventoryRepository.findByBranchIdAndSparePartId(1L, 1L)).thenReturn(Optional.of(colomboInv));

        BranchInventory galleInv = new BranchInventory();
        galleInv.setQuantity(0);
        when(inventoryRepository.findByBranchIdAndSparePartId(2L, 1L)).thenReturn(Optional.of(galleInv));

        // Request from Galle area
        BranchRecommendationRequestDto request = new BranchRecommendationRequestDto(
                6.0500, 80.2200, 1L, "Apple", "iPhone 13"
        );

        BranchRecommendationResponseDto response = branchService.getBranchRecommendation(request);

        assertNotNull(response);
        assertEquals(2L, response.getRecommendedBranch().getId()); // Nearest Galle chosen as fallback
        assertFalse(response.getIsPartAvailable()); // Flag parts out of stock
        assertTrue(response.getReason().contains("Warning: Required spare parts are currently out of stock"));
    }
}
