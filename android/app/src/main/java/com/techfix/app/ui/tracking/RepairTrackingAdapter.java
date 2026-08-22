package com.techfix.app.ui.tracking;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.data.remote.dto.RepairStatusHistoryDto;
import com.techfix.app.data.remote.dto.RepairTrackingDto;

import java.util.ArrayList;
import java.util.List;

public class RepairTrackingAdapter extends RecyclerView.Adapter<RepairTrackingAdapter.ViewHolder> {

    private final Context context;
    private RepairTrackingDto trackingData;
    private final List<RepairStatusHistoryDto> history = new ArrayList<>();
    private String currentStatus = "REQUEST_SUBMITTED";

    private final String[] STAGES = {
            "REQUEST_SUBMITTED",
            "BRANCH_ASSIGNED",
            "DEVICE_RECEIVED",
            "DIAGNOSIS",
            "REPAIRING",
            "QUALITY_CHECK",
            "READY_FOR_COLLECTION",
            "COMPLETED"
    };

    private final String[] STAGE_DISPLAY_NAMES = {
            "Request Submitted",
            "Branch Assigned",
            "Device Received",
            "Diagnostic & Inspection",
            "Repair in Progress",
            "Quality Check (QA)",
            "Ready for Collection",
            "Repair Completed"
    };

    private final String[] STAGE_DESCRIPTIONS = {
            "Customer submitted repair request online",
            "Nearest branch and technician allocated",
            "Device handed over at branch counter",
            "Technician inspecting hardware faults",
            "Component replacement and soldering in progress",
            "Testing functionality, display, battery & sensors",
            "Device tested, cleaned, and ready for pickup",
            "Device collected and payment settled"
    };

    public RepairTrackingAdapter(Context context) {
        this.context = context;
    }

    public void setTrackingData(RepairTrackingDto trackingData) {
        this.trackingData = trackingData;
        this.history.clear();
        if (trackingData != null) {
            if (trackingData.getHistory() != null) {
                this.history.addAll(trackingData.getHistory());
            }
            if (trackingData.getCurrentStatus() != null) {
                this.currentStatus = trackingData.getCurrentStatus();
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tracking_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String stageKey = STAGES[position];
        String displayName = STAGE_DISPLAY_NAMES[position];
        String defaultDesc = STAGE_DESCRIPTIONS[position];

        // Search in history for a matching completed record
        RepairStatusHistoryDto matchedRecord = null;
        for (RepairStatusHistoryDto record : history) {
            if (stageKey.equals(record.getStatus())) {
                matchedRecord = record;
                break;
            }
        }

        int currentStep = getStepNumber(currentStatus);
        int itemStep = position + 1;

        // Hide connector line for the last element
        if (position == getItemCount() - 1) {
            holder.viewConnectorLine.setVisibility(View.INVISIBLE);
        } else {
            holder.viewConnectorLine.setVisibility(View.VISIBLE);
        }

        if (itemStep < currentStep) {
            // COMPLETED STATE (Green Checkmark)
            holder.ivStepIndicator.setBackgroundResource(R.drawable.bg_circle_solid);
            holder.ivStepIndicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_completed)));
            holder.ivStepIndicator.setImageResource(R.drawable.ic_check);
            holder.ivStepIndicator.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.ivStepIndicator.setPadding(6, 6, 6, 6);

            holder.viewConnectorLine.setBackgroundColor(ContextCompat.getColor(context, R.color.status_completed));

            holder.tvStepName.setText(displayName);
            holder.tvStepName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));

            if (matchedRecord != null) {
                holder.tvStepTime.setText(formatDate(matchedRecord.getTimestamp()));
                holder.tvStepTime.setVisibility(View.VISIBLE);

                if (matchedRecord.getNotes() != null && !matchedRecord.getNotes().trim().isEmpty()) {
                    holder.tvStepNotes.setText(matchedRecord.getNotes());
                    holder.tvStepNotes.setVisibility(View.VISIBLE);
                } else {
                    holder.tvStepNotes.setText(defaultDesc);
                    holder.tvStepNotes.setVisibility(View.VISIBLE);
                }

                if (matchedRecord.getUpdatedByName() != null && !matchedRecord.getUpdatedByName().trim().isEmpty()) {
                    holder.tvStepUpdater.setText("By: " + matchedRecord.getUpdatedByName());
                    holder.tvStepUpdater.setVisibility(View.VISIBLE);
                } else {
                    holder.tvStepUpdater.setVisibility(View.GONE);
                }
            } else {
                holder.tvStepTime.setVisibility(View.GONE);
                holder.tvStepNotes.setText(defaultDesc);
                holder.tvStepNotes.setVisibility(View.VISIBLE);
                holder.tvStepUpdater.setVisibility(View.GONE);
            }

        } else if (itemStep == currentStep) {
            // CURRENT ACTIVE STATE (Blue Dot)
            holder.ivStepIndicator.setBackgroundResource(R.drawable.bg_circle_solid);
            holder.ivStepIndicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary)));
            holder.ivStepIndicator.setImageResource(R.drawable.ic_dot);
            holder.ivStepIndicator.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.ivStepIndicator.setPadding(6, 6, 6, 6);

            holder.viewConnectorLine.setBackgroundColor(ContextCompat.getColor(context, R.color.border));

            holder.tvStepName.setText(displayName);
            holder.tvStepName.setTextColor(ContextCompat.getColor(context, R.color.primary));

            if (matchedRecord != null) {
                holder.tvStepTime.setText(formatDate(matchedRecord.getTimestamp()));
                holder.tvStepTime.setVisibility(View.VISIBLE);

                if (matchedRecord.getNotes() != null && !matchedRecord.getNotes().trim().isEmpty()) {
                    holder.tvStepNotes.setText(matchedRecord.getNotes());
                    holder.tvStepNotes.setVisibility(View.VISIBLE);
                } else {
                    holder.tvStepNotes.setText(defaultDesc);
                    holder.tvStepNotes.setVisibility(View.VISIBLE);
                }

                if (matchedRecord.getUpdatedByName() != null && !matchedRecord.getUpdatedByName().trim().isEmpty()) {
                    holder.tvStepUpdater.setText("By: " + matchedRecord.getUpdatedByName());
                    holder.tvStepUpdater.setVisibility(View.VISIBLE);
                } else {
                    holder.tvStepUpdater.setVisibility(View.GONE);
                }
            } else {
                holder.tvStepTime.setVisibility(View.GONE);
                holder.tvStepNotes.setText(defaultDesc);
                holder.tvStepNotes.setVisibility(View.VISIBLE);
                holder.tvStepUpdater.setVisibility(View.GONE);
            }

        } else {
            // PENDING STATE (Gray Circle Outline)
            holder.ivStepIndicator.setBackgroundResource(R.drawable.bg_circle_outline);
            holder.ivStepIndicator.setBackgroundTintList(null);
            holder.ivStepIndicator.setImageDrawable(null);

            holder.viewConnectorLine.setBackgroundColor(ContextCompat.getColor(context, R.color.border));

            holder.tvStepName.setText(displayName);
            holder.tvStepName.setTextColor(ContextCompat.getColor(context, R.color.text_muted));

            holder.tvStepTime.setVisibility(View.GONE);
            holder.tvStepNotes.setVisibility(View.GONE);
            holder.tvStepUpdater.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return STAGES.length;
    }

    private int getStepNumber(String status) {
        if (status == null) return 1;
        switch (status) {
            case "REQUEST_SUBMITTED": return 1;
            case "BRANCH_ASSIGNED": return 2;
            case "DEVICE_RECEIVED": return 3;
            case "DIAGNOSIS": return 4;
            case "REPAIRING": return 5;
            case "QUALITY_CHECK": return 6;
            case "READY_FOR_COLLECTION": return 7;
            case "COMPLETED": return 8;
            default: return 1;
        }
    }

    private String formatDate(String isoTimestamp) {
        if (isoTimestamp == null || isoTimestamp.isEmpty()) return "";
        try {
            // Keep it simple and user-friendly. e.g. "2026-08-22T10:15:30" -> "2026-08-22 10:15"
            if (isoTimestamp.contains("T")) {
                String datePart = isoTimestamp.substring(0, 10);
                String timePart = isoTimestamp.substring(11, 16);
                return datePart + " " + timePart;
            }
            return isoTimestamp;
        } catch (Exception e) {
            return isoTimestamp;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewConnectorLine;
        ImageView ivStepIndicator;
        TextView tvStepName;
        TextView tvStepTime;
        TextView tvStepUpdater;
        TextView tvStepNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewConnectorLine = itemView.findViewById(R.id.viewConnectorLine);
            ivStepIndicator = itemView.findViewById(R.id.ivStepIndicator);
            tvStepName = itemView.findViewById(R.id.tvStepName);
            tvStepTime = itemView.findViewById(R.id.tvStepTime);
            tvStepUpdater = itemView.findViewById(R.id.tvStepUpdater);
            tvStepNotes = itemView.findViewById(R.id.tvStepNotes);
        }
    }
}
