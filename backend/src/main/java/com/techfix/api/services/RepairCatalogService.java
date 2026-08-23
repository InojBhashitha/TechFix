package com.techfix.api.services;

import com.techfix.api.entities.DeviceCategory;
import com.techfix.api.entities.RepairService;
import com.techfix.api.repositories.DeviceCategoryRepository;
import com.techfix.api.repositories.RepairServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("null")
public class RepairCatalogService {

    private final DeviceCategoryRepository categoryRepository;
    private final RepairServiceRepository serviceRepository;

    public RepairCatalogService(DeviceCategoryRepository categoryRepository,
                                RepairServiceRepository serviceRepository) {
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<DeviceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<RepairService> getAllServices() {
        return serviceRepository.findByIsActiveTrue();
    }

    public List<RepairService> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    public RepairService getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair service not found with ID: " + id));
    }
}
