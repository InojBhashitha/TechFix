package com.techfix.app.ui.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techfix.app.R;

import java.util.ArrayList;
import java.util.List;

public class RepairImageAdapter extends RecyclerView.Adapter<RepairImageAdapter.ImageViewHolder> {

    private List<Uri> imageUris = new ArrayList<>();
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onImageClick(Uri uri);
        void onImageDelete(Uri uri, int position);
    }

    public RepairImageAdapter(OnImageActionListener listener) {
        this.listener = listener;
    }

    public void setImageUris(List<Uri> uris) {
        this.imageUris = uris;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(uri)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivThumbnail);

        holder.ivThumbnail.setOnClickListener(v -> listener.onImageClick(uri));
        holder.btnRemoveImage.setOnClickListener(v -> listener.onImageDelete(uri, position));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        View btnRemoveImage;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnRemoveImage = itemView.findViewById(R.id.btnRemoveImage);
        }
    }
}
