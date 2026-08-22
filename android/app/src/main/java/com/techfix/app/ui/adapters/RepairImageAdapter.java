package com.techfix.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techfix.app.R;

import java.util.ArrayList;
import java.util.List;

public class RepairImageAdapter extends RecyclerView.Adapter<RepairImageAdapter.ImageViewHolder> {

    private List<SelectedImage> imageList = new ArrayList<>();
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onImageClick(SelectedImage image);
        void onImageDelete(SelectedImage image, int position);
    }

    public RepairImageAdapter(OnImageActionListener listener) {
        this.listener = listener;
    }

    public void setImageList(List<SelectedImage> list) {
        this.imageList = list;
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
        SelectedImage item = imageList.get(position);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getUri())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivThumbnail);

        // Update state views visibility
        SelectedImage.State state = item.getState();
        holder.pbUpload.setVisibility(state == SelectedImage.State.UPLOADING ? View.VISIBLE : View.GONE);
        holder.ivUploadSuccess.setVisibility(state == SelectedImage.State.UPLOADED ? View.VISIBLE : View.GONE);
        holder.ivUploadFailed.setVisibility(state == SelectedImage.State.FAILED ? View.VISIBLE : View.GONE);

        // Disable deletion if uploading or uploaded
        boolean canDelete = (state == SelectedImage.State.PENDING || state == SelectedImage.State.FAILED);
        holder.btnRemoveImage.setVisibility(canDelete ? View.VISIBLE : View.GONE);

        holder.ivThumbnail.setOnClickListener(v -> listener.onImageClick(item));
        holder.btnRemoveImage.setOnClickListener(v -> {
            if (canDelete) {
                listener.onImageDelete(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        ProgressBar pbUpload;
        ImageView ivUploadSuccess;
        ImageView ivUploadFailed;
        View btnRemoveImage;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            pbUpload = itemView.findViewById(R.id.pbUpload);
            ivUploadSuccess = itemView.findViewById(R.id.ivUploadSuccess);
            ivUploadFailed = itemView.findViewById(R.id.ivUploadFailed);
            btnRemoveImage = itemView.findViewById(R.id.btnRemoveImage);
        }
    }
}
