package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.dto.BookingRequestDto;
import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @Valid @RequestBody BookingRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            BookingResponseDto response = bookingService.createBooking(request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Repair booking submitted successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<BookingResponseDto> bookings = bookingService.getCustomerBookings(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Bookings retrieved", bookings));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingByReference(@PathVariable String reference) {
        try {
            BookingResponseDto booking = bookingService.getBookingByReference(reference);
            return ResponseEntity.ok(ApiResponse.success("Booking details retrieved", booking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{reference}/tracking")
    public ResponseEntity<ApiResponse<com.techfix.api.dto.RepairTrackingDto>> getBookingTracking(
            @PathVariable String reference,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            com.techfix.api.dto.RepairTrackingDto tracking = bookingService.getBookingTracking(reference, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Repair tracking details retrieved successfully", tracking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{reference}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDto>> updateBookingStatus(
            @PathVariable String reference,
            @RequestParam com.techfix.api.enums.RepairStatus status,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            BookingResponseDto response = bookingService.updateBookingStatus(reference, status, notes, userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Booking status updated successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
