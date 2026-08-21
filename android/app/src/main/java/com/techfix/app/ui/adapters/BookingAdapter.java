package com.techfix.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.remote.dto.BookingResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingResponseDto> bookings = new ArrayList<>();
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(BookingResponseDto booking);
    }

    public BookingAdapter(OnBookingClickListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<BookingResponseDto> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingResponseDto booking = bookings.get(position);

        holder.tvBookingRef.setText(booking.getBookingReference());
        holder.tvServiceName.setText(booking.getServiceName());
        holder.tvDeviceInfo.setText(booking.getDeviceBrand() + " " + booking.getDeviceModel() + " • " + booking.getBranchName());

        if (booking.getTotalCost() != null) {
            holder.tvTotalCost.setText("LKR " + String.format(Locale.getDefault(), "%,.2f", booking.getTotalCost()));
        } else {
            holder.tvTotalCost.setText("");
        }

        if (booking.getAppointmentDate() != null) {
            holder.tvAppointmentDate.setText("Appt: " + booking.getAppointmentDate().replace("T", " "));
        } else {
            holder.tvAppointmentDate.setText("");
        }

        // Status badge display and styling
        String statusText = booking.getStatusDisplayName() != null ? booking.getStatusDisplayName() : booking.getCurrentStatus();
        holder.tvStatusBadge.setText(statusText);

        int colorRes = R.color.status_submitted;
        if ("COMPLETED".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_completed;
        } else if ("READY_FOR_COLLECTION".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_ready;
        } else if ("REPAIRING".equals(booking.getCurrentStatus()) || "DIAGNOSIS".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_in_progress;
        } else if ("CANCELLED".equals(booking.getCurrentStatus())) {
            colorRes = R.color.status_cancelled;
        }

        holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        holder.itemView.setOnClickListener(v -> listener.onBookingClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingRef, tvStatusBadge, tvServiceName, tvDeviceInfo, tvAppointmentDate, tvTotalCost;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingRef = itemView.findViewById(R.id.tvBookingRef);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceInfo = itemView.findViewById(R.id.tvDeviceInfo);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvTotalCost = itemView.findViewById(R.id.tvTotalCost);
        }
    }
}
