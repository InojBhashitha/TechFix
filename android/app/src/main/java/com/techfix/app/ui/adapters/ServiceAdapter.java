package com.techfix.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.dto.RepairServiceDto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<RepairServiceDto> services = new ArrayList<>();
    private final OnServiceClickListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));

    public interface OnServiceClickListener {
        void onBookService(RepairServiceDto service);
    }

    public ServiceAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    public void setServices(List<RepairServiceDto> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_service_card, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        RepairServiceDto service = services.get(position);

        holder.tvServiceName.setText(service.getName());
        holder.tvServiceDescription.setText(service.getDescription());

        if (service.getCategory() != null) {
            holder.tvCategoryName.setText(service.getCategory().getName());
        } else {
            holder.tvCategoryName.setText("General");
        }

        if (service.getEstimatedPrice() != null) {
            holder.tvPriceBadge.setText("LKR " + String.format(Locale.getDefault(), "%,.2f", service.getEstimatedPrice()));
        } else {
            holder.tvPriceBadge.setText("Quote on inspect");
        }

        if (service.getEstimatedDurationMinutes() != null) {
            holder.tvDuration.setText("Est. " + service.getEstimatedDurationMinutes() + " mins");
        } else {
            holder.tvDuration.setText("Est. 60 mins");
        }

        // Load image with Glide if URL exists
        if (service.getSampleImageUrl() != null && !service.getSampleImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(service.getSampleImageUrl())
                    .placeholder(R.drawable.ic_build)
                    .error(R.drawable.ic_build)
                    .into(holder.ivServiceImage);
        } else {
            holder.ivServiceImage.setImageResource(R.drawable.ic_build);
        }

        holder.btnBookService.setOnClickListener(v -> listener.onBookService(service));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivServiceImage;
        TextView tvServiceName, tvCategoryName, tvServiceDescription, tvPriceBadge, tvDuration;
        MaterialButton btnBookService;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceImage = itemView.findViewById(R.id.ivServiceImage);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
            tvPriceBadge = itemView.findViewById(R.id.tvPriceBadge);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnBookService = itemView.findViewById(R.id.btnBookService);
        }
    }
}
