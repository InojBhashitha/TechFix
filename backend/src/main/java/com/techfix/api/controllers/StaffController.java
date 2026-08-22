package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.dto.AssignTechnicianRequestDto;
import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.dto.InventoryStockDto;
import com.techfix.api.dto.StaffDashboardStatsDto;
import com.techfix.api.dto.TechnicianDto;
import com.techfix.api.dto.UpdateInventoryStockRequestDto;
import com.techfix.api.dto.UpdateRepairStatusRequestDto;
import com.techfix.api.services.StaffService;
import com.techfix.api.enums.RepairStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<StaffDashboardStatsDto>> getDashboardStats(
            @RequestParam(required = false) Long branchId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            StaffDashboardStatsDto stats = staffService.getDashboardStats(userDetails.getUsername(), branchId);
            return ResponseEntity.ok(ApiResponse.success("Staff dashboard stats retrieved successfully", stats));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getStaffBookings(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) RepairStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<BookingResponseDto> bookings = staffService.getStaffBookings(userDetails.getUsername(), branchId, status);
            return ResponseEntity.ok(ApiResponse.success("Staff bookings retrieved successfully", bookings));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponseDto>> updateRepairStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateRepairStatusRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            BookingResponseDto updatedBooking = staffService.updateRepairStatus(id, request, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Repair status updated successfully", updatedBooking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/bookings/{id}/assign-technician")
    public ResponseEntity<ApiResponse<BookingResponseDto>> assignTechnician(
            @PathVariable String id,
            @Valid @RequestBody AssignTechnicianRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            BookingResponseDto updatedBooking = staffService.assignTechnician(id, request, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", updatedBooking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/technicians")
    public ResponseEntity<ApiResponse<List<TechnicianDto>>> getTechnicians(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Boolean availableOnly,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<TechnicianDto> technicians = staffService.getTechnicians(userDetails.getUsername(), branchId, availableOnly);
            return ResponseEntity.ok(ApiResponse.success("Technicians retrieved successfully", technicians));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<InventoryStockDto>>> getInventory(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Boolean lowStockOnly,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<InventoryStockDto> inventory = staffService.getInventory(userDetails.getUsername(), branchId, lowStockOnly);
            return ResponseEntity.ok(ApiResponse.success("Branch inventory retrieved successfully", inventory));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/inventory/{id}/stock")
    public ResponseEntity<ApiResponse<InventoryStockDto>> updateInventoryStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInventoryStockRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            InventoryStockDto updatedInventory = staffService.updateInventoryStock(id, request, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Inventory stock updated successfully", updatedInventory));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
