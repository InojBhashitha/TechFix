package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.entities.DeviceCategory;
import com.techfix.api.entities.RepairService;
import com.techfix.api.services.RepairCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceController {

    private final RepairCatalogService catalogService;

    public ServiceController(RepairCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<DeviceCategory>>> getCategories() {
        List<DeviceCategory> categories = catalogService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Device categories retrieved", categories));
    }

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<RepairService>>> getServices(
            @RequestParam(required = false) Long categoryId) {
        List<RepairService> services;
        if (categoryId != null) {
            services = catalogService.getServicesByCategory(categoryId);
        } else {
            services = catalogService.getAllServices();
        }
        return ResponseEntity.ok(ApiResponse.success("Repair services retrieved", services));
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ApiResponse<RepairService>> getServiceById(@PathVariable Long id) {
        try {
            RepairService service = catalogService.getServiceById(id);
            return ResponseEntity.ok(ApiResponse.success("Service retrieved", service));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
