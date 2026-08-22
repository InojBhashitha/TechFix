package com.techfix.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.dto.BookingResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffRepairQueueAdapter extends RecyclerView.Adapter<StaffRepairQueueAdapter.ViewHolder> {

    private List<BookingResponseDto> bookings = new ArrayList<>();
    private final OnStaffRepairActionListener listener;

    public interface OnStaffRepairActionListener {
        void onUpdateStatus(BookingResponseDto booking);
        void onAssignTechnician(BookingResponseDto booking);
        void onItemClick(BookingResponseDto booking);
    }

    public StaffRepairQueueAdapter(OnStaffRepairActionListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<BookingResponseDto> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_repair_queue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingResponseDto booking = bookings.get(position);

        holder.tvBookingRef.setText(booking.getBookingReference() != null ? booking.getBookingReference() : "");
        holder.tvServiceDevice.setText(
                (booking.getServiceName() != null ? booking.getServiceName() : "") + " • " +
                (booking.getDeviceBrand() != null ? booking.getDeviceBrand() : "") + " " +
                (booking.getDeviceModel() != null ? booking.getDeviceModel() : "")
        );

        String customerText = "Customer: " + (booking.getCustomerName() != null ? booking.getCustomerName() : "N/A");
        if (booking.getCustomerEmail() != null) {
            customerText += " (" + booking.getCustomerEmail() + ")";
        }
        holder.tvCustomerInfo.setText(customerText);

        if (booking.getTechnicianName() != null && !booking.getTechnicianName().isBlank()) {
            holder.tvTechnician.setText("Tech: " + booking.getTechnicianName());
            holder.tvTechnician.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary));
        } else {
            holder.tvTechnician.setText("Tech: Unassigned");
            holder.tvTechnician.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_in_progress));
        }

        holder.tvBranchName.setText(booking.getBranchName() != null ? booking.getBranchName() : "");

        if (booking.getAppointmentDate() != null) {
            holder.tvApptDate.setText("Appt: " + booking.getAppointmentDate().replace("T", " "));
        } else {
            holder.tvApptDate.setText("");
        }

        if (booking.getTotalCost() != null) {
            holder.tvPrice.setText("LKR " + String.format(Locale.getDefault(), "%,.2f", booking.getTotalCost()));
        } else {
            holder.tvPrice.setText("");
        }

        // Status badge styling
        String statusText = booking.getStatusDisplayName() != null ? booking.getStatusDisplayName() : booking.getCurrentStatus();
        holder.tvStatusBadge.setText(statusText);

        int colorRes = R.color.status_submitted;
        if ("COMPLETED".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_completed;
        } else if ("READY_FOR_COLLECTION".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_ready;
        } else if ("REPAIRING".equals(booking.getCurrentStatus()) || "DIAGNOSIS".equals(booking.getCurrentStatus()) || "QUALITY_CHECK".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_in_progress;
        } else if ("CANCELLED".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_cancelled;
        }

        holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        // Click Listeners
        holder.btnUpdateStatus.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateStatus(booking);
        });

        holder.btnAssignTech.setOnClickListener(v -> {
            if (listener != null) listener.onAssignTechnician(booking);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(booking);
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingRef, tvStatusBadge, tvServiceDevice, tvCustomerInfo, tvTechnician, tvBranchName, tvApptDate, tvPrice;
        MaterialButton btnAssignTech, btnUpdateStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingRef = itemView.findViewById(R.id.tvBookingRef);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvServiceDevice = itemView.findViewById(R.id.tvServiceDevice);
            tvCustomerInfo = itemView.findViewById(R.id.tvCustomerInfo);
            tvTechnician = itemView.findViewById(R.id.tvTechnician);
            tvBranchName = itemView.findViewById(R.id.tvBranchName);
            tvApptDate = itemView.findViewById(R.id.tvApptDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAssignTech = itemView.findViewById(R.id.btnAssignTech);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
