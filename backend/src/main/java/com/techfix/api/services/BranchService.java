package com.techfix.api.services;

import com.techfix.api.dto.BranchDto;
import com.techfix.api.dto.BranchRecommendationRequestDto;
import com.techfix.api.dto.BranchRecommendationResponseDto;
import com.techfix.api.dto.BranchRecommendationResponseDto.BranchDetailDto;
import com.techfix.api.entities.Branch;
import com.techfix.api.entities.BranchInventory;
import com.techfix.api.entities.RepairService;
import com.techfix.api.entities.Technician;
import com.techfix.api.repositories.BranchInventoryRepository;
import com.techfix.api.repositories.BranchRepository;
import com.techfix.api.repositories.RepairServiceRepository;
import com.techfix.api.repositories.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchInventoryRepository inventoryRepository;
    private final TechnicianRepository technicianRepository;
    private final RepairServiceRepository serviceRepository;

    public BranchService(BranchRepository branchRepository,
                         BranchInventoryRepository inventoryRepository,
                         TechnicianRepository technicianRepository,
                         RepairServiceRepository serviceRepository) {
        this.branchRepository = branchRepository;
        this.inventoryRepository = inventoryRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<BranchDto> getAllActiveBranches() {
        return branchRepository.findByIsActiveTrue().stream()
                .map(BranchDto::new)
                .collect(Collectors.toList());
    }

    public BranchRecommendationResponseDto getBranchRecommendation(BranchRecommendationRequestDto request) {
        List<Branch> branches = branchRepository.findByIsActiveTrue();
        if (branches.isEmpty()) {
            throw new RuntimeException("No active branches available");
        }

        Double customerLat = request.getLatitude();
        Double customerLng = request.getLongitude();
        Long serviceId = request.getServiceId();
        String brand = request.getDeviceBrand();
        String model = request.getDeviceModel();

        // 1. Determine if a spare part is required
        Long requiredPartId = determineRequiredSparePartId(serviceId, brand, model);

        // 2. Fetch repair service category details for technician capability check
        String categoryName = null;
        if (serviceId != null) {
            Optional<RepairService> serviceOpt = serviceRepository.findById(serviceId);
            if (serviceOpt.isPresent()) {
                categoryName = serviceOpt.get().getCategory().getName();
            }
        }

        List<BranchDetailDto> details = new ArrayList<>();
        for (Branch b : branches) {
            // Calculate Haversine distance
            double distance = calculateHaversineDistance(customerLat, customerLng, b.getLatitude(), b.getLongitude());

            // Check technician availability and capability
            List<Technician> availableTechs = technicianRepository.findByBranchIdAndIsAvailableTrue(b.getId());
            boolean isTechAvailable = false;
            
            if (!availableTechs.isEmpty()) {
                if (categoryName != null) {
                    for (Technician tech : availableTechs) {
                        if (isTechnicianCompatible(tech.getSpecialization(), categoryName)) {
                            isTechAvailable = true;
                            break;
                        }
                    }
                } else {
                    isTechAvailable = true;
                }
            }

            // Check spare part stock availability
            boolean isPartAvailable = true;
            if (requiredPartId != null) {
                Optional<BranchInventory> inventory = inventoryRepository.findByBranchIdAndSparePartId(b.getId(), requiredPartId);
                isPartAvailable = inventory.isPresent() && inventory.get().getQuantity() > 0;
            }

            // Standard opening hours
            String openingHours = "08:00 AM - 06:00 PM";

            details.add(new BranchDetailDto(new BranchDto(b), distance, isTechAvailable, isPartAvailable, openingHours));
        }

        // 3. Routing Decision Tree
        // Filter those where BOTH parts and technicians are available
        List<BranchDetailDto> fullyEligible = details.stream()
                .filter(d -> d.getIsTechnicianAvailable() && d.getIsPartAvailable())
                .sorted(Comparator.comparing(BranchDetailDto::getDistanceKm))
                .collect(Collectors.toList());

        BranchDetailDto recommendation = null;
        String reason = "";

        if (!fullyEligible.isEmpty()) {
            recommendation = fullyEligible.get(0);
            reason = "Recommended based on shortest distance (" + String.format("%.1f", recommendation.getDistanceKm()) + " km) with available technicians and required spare parts in stock.";
        } else {
            // Fallback: Filter by parts availability first (cannot repair without parts)
            List<BranchDetailDto> partsEligible = details.stream()
                    .filter(BranchDetailDto::getIsPartAvailable)
                    .sorted(Comparator.comparing(BranchDetailDto::getDistanceKm))
                    .collect(Collectors.toList());

            if (!partsEligible.isEmpty()) {
                recommendation = partsEligible.get(0);
                reason = "Recommended based on spare-parts availability. Note: Technicians are currently busy; booking is accepted but might experience slight delays.";
            } else {
                // If parts are not available anywhere, select the closest branch overall
                List<BranchDetailDto> sortedByDistance = details.stream()
                        .sorted(Comparator.comparing(BranchDetailDto::getDistanceKm))
                        .collect(Collectors.toList());
                recommendation = sortedByDistance.get(0);
                reason = "Recommended nearest branch overall. Warning: Required spare parts are currently out of stock. Contact customer support to check stock arrival.";
            }
        }

        return new BranchRecommendationResponseDto(
                recommendation.getBranch(),
                recommendation.getDistanceKm(),
                recommendation.getIsTechnicianAvailable(),
                recommendation.getIsPartAvailable(),
                recommendation.getOpeningHours(),
                reason,
                details
        );
    }

    /**
     * Helper to verify if a technician's specialization supports a given repair category name.
     */
    private boolean isTechnicianCompatible(String specialization, String categoryName) {
        if (specialization == null || categoryName == null) {
            return true;
        }
        String specLower = specialization.toLowerCase();
        String catLower = categoryName.toLowerCase();

        if (catLower.contains("mobile") || catLower.contains("phone") || catLower.contains("tablet")) {
            return specLower.contains("mobile") || specLower.contains("phone") || specLower.contains("tablet") 
                    || specLower.contains("display") || specLower.contains("hardware");
        }
        if (catLower.contains("laptop")) {
            return specLower.contains("laptop") || specLower.contains("screen") || specLower.contains("motherboard") 
                    || specLower.contains("thermal") || specLower.contains("micro-soldering");
        }
        if (catLower.contains("desktop") || catLower.contains("computer")) {
            return specLower.contains("desktop") || specLower.contains("pc") || specLower.contains("power");
        }
        return true;
    }

    /**
     * Determines which spare part ID is required for a repair service based on category rules.
     */
    private Long determineRequiredSparePartId(Long serviceId, String brand, String model) {
        if (serviceId == null) {
            return null;
        }

        // Service ID 1: Screen Replacement
        if (serviceId == 1L) {
            String brandLower = brand != null ? brand.toLowerCase() : "";
            String modelLower = model != null ? model.toLowerCase() : "";
            if (brandLower.contains("apple") || brandLower.contains("iphone") || modelLower.contains("iphone")) {
                return 1L; // iPhone 13 OLED Display Assembly
            } else if (brandLower.contains("samsung") || modelLower.contains("s22")) {
                return 2L; // Samsung Galaxy S22 AMOLED Panel
            }
            return 1L; // Default to iPhone 13 Screen for general phones
        }

        // Service ID 2: Battery Replacement
        if (serviceId == 2L) {
            return 3L; // Universal High Capacity Mobile Battery 5000mAh
        }

        // Service ID 5: Laptop Screen Replacement
        if (serviceId == 5L) {
            return 4L; // 15.6 Inch Full HD IPS Laptop Panel 144Hz
        }

        // Service ID 6: Keyboard Replacement
        if (serviceId == 6L) {
            return 5L; // Universal Backlit Laptop Keyboard Assembly
        }

        // Service ID 9: Power Supply (PSU) Replacement
        if (serviceId == 9L) {
            return 6L; // 650W 80 Plus Bronze Power Supply Unit
        }

        // Other service IDs do not require hardware parts tracked in inventory (e.g. software, cleanups)
        return null;
    }

    /**
     * Calculates the distance in kilometers between two points using the Haversine formula.
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radious of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
