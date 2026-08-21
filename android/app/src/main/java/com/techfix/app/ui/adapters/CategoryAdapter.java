package com.techfix.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.techfix.app.R;
import com.techfix.app.data.remote.dto.DeviceCategoryDto;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<DeviceCategoryDto> categories = new ArrayList<>();
    private Long selectedCategoryId = null; // null means "All"
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(DeviceCategoryDto category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<DeviceCategoryDto> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setSelectedCategoryId(Long categoryId) {
        this.selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (position == 0) {
            // "All" chip
            holder.chip.setText("All Categories");
            boolean isSelected = (selectedCategoryId == null);
            holder.chip.setChecked(isSelected);

            holder.chip.setOnClickListener(v -> {
                selectedCategoryId = null;
                notifyDataSetChanged();
                listener.onCategoryClick(null);
            });
        } else {
            DeviceCategoryDto category = categories.get(position - 1);
            holder.chip.setText(category.getName());
            boolean isSelected = selectedCategoryId != null && selectedCategoryId.equals(category.getId());
            holder.chip.setChecked(isSelected);

            holder.chip.setOnClickListener(v -> {
                selectedCategoryId = category.getId();
                notifyDataSetChanged();
                listener.onCategoryClick(category);
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories.size() + 1; // +1 for "All" chip
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chipCategory);
        }
    }
}
