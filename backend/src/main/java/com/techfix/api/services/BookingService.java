package com.techfix.api.services;

import com.techfix.api.dto.BookingRequestDto;
import com.techfix.api.dto.BookingResponseDto;
import com.techfix.api.entities.*;
import com.techfix.api.enums.RepairStatus;
import com.techfix.api.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final RepairRequestRepository bookingRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final RepairServiceRepository serviceRepository;
    private final RepairStatusHistoryRepository statusHistoryRepository;

    public BookingService(RepairRequestRepository bookingRepository,
                          UserRepository userRepository,
                          BranchRepository branchRepository,
                          RepairServiceRepository serviceRepository,
                          RepairStatusHistoryRepository statusHistoryRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.serviceRepository = serviceRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + customerEmail));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + request.getBranchId()));

        RepairService service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Repair service not found with ID: " + request.getServiceId()));

        RepairRequest booking = new RepairRequest();
        booking.setBookingReference(generateBookingReference());
        booking.setCustomer(customer);
        booking.setBranch(branch);
        booking.setService(service);
        booking.setDeviceBrand(request.getDeviceBrand());
        booking.setDeviceModel(request.getDeviceModel());
        booking.setSerialNumber(request.getSerialNumber());
        booking.setProblemDescription(request.getProblemDescription());
        booking.setAppointmentDate(request.getAppointmentDate() != null ? request.getAppointmentDate() : LocalDateTime.now().plusDays(1));
        booking.setCurrentStatus(RepairStatus.REQUEST_SUBMITTED);
        booking.setCustomerLatitude(request.getCustomerLatitude());
        booking.setCustomerLongitude(request.getCustomerLongitude());
        booking.setTotalCost(service.getEstimatedPrice());

        booking = bookingRepository.save(booking);

        // Record initial status history
        RepairStatusHistory initialStatus = new RepairStatusHistory(
                booking,
                RepairStatus.REQUEST_SUBMITTED,
                "Repair request created online by customer",
                customer
        );
        statusHistoryRepository.save(initialStatus);

        return mapToResponseDto(booking);
    }

    public List<BookingResponseDto> getCustomerBookings(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public BookingResponseDto getBookingByReference(String reference) {
        RepairRequest booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found with reference: " + reference));
        return mapToResponseDto(booking);
    }

    private String generateBookingReference() {
        int randomNum = 1000 + new Random().nextInt(9000);
        return "TF-2026-" + randomNum;
    }

    public BookingResponseDto mapToResponseDto(RepairRequest booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setCustomerName(booking.getCustomer().getFullName());
        dto.setCustomerEmail(booking.getCustomer().getEmail());
        dto.setBranchName(booking.getBranch().getName());
        dto.setServiceName(booking.getService().getName());
        if (booking.getService().getCategory() != null) {
            dto.setCategoryName(booking.getService().getCategory().getName());
        }
        dto.setDeviceBrand(booking.getDeviceBrand());
        dto.setDeviceModel(booking.getDeviceModel());
        dto.setSerialNumber(booking.getSerialNumber());
        dto.setProblemDescription(booking.getProblemDescription());
        dto.setAppointmentDate(booking.getAppointmentDate());
        dto.setCurrentStatus(booking.getCurrentStatus());
        dto.setTotalCost(booking.getTotalCost());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }
}
