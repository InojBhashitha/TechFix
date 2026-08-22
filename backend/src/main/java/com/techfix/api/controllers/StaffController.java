package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.dto.StaffDashboardStatsDto;
import com.techfix.api.dto.UpdateRepairStatusRequestDto;
import com.techfix.api.services.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
}
