package com.techfix.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.dto.InventoryStockDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffInventoryAdapter extends RecyclerView.Adapter<StaffInventoryAdapter.ViewHolder> {

    private List<InventoryStockDto> items = new ArrayList<>();
    private final OnInventoryActionListener listener;

    public interface OnInventoryActionListener {
        void onUpdateStock(InventoryStockDto item);
    }

    public StaffInventoryAdapter(OnInventoryActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<InventoryStockDto> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryStockDto item = items.get(position);

        holder.tvPartCode.setText(item.getPartCode() != null ? item.getPartCode() : "");
        holder.tvPartName.setText(item.getPartName() != null ? item.getPartName() : "Spare Part");

        String categoryBranch = (item.getCategoryName() != null ? item.getCategoryName() : "Category") +
                " • " + (item.getBranchName() != null ? item.getBranchName() : "");
        holder.tvCategoryBranch.setText(categoryBranch);

        int qty = item.getQuantity() != null ? item.getQuantity() : 0;
        int minAlert = item.getMinimumStockAlert() != null ? item.getMinimumStockAlert() : 0;
        holder.tvQuantity.setText("In Stock: " + qty + " units");
        holder.tvMinAlert.setText("Min Alert Level: " + minAlert);

        if (item.getUnitCost() != null) {
            holder.tvUnitCost.setText("LKR " + String.format(Locale.getDefault(), "%,.2f", item.getUnitCost()));
        } else {
            holder.tvUnitCost.setText("");
        }

        if (Boolean.TRUE.equals(item.getIsLowStock())) {
            holder.tvLowStockBadge.setVisibility(View.VISIBLE);
            holder.tvLowStockBadge.setText("Low Stock Alert! (Min: " + minAlert + ")");
        } else {
            holder.tvLowStockBadge.setVisibility(View.GONE);
        }

        holder.btnUpdateStock.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateStock(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartCode, tvLowStockBadge, tvPartName, tvCategoryBranch, tvQuantity, tvMinAlert, tvUnitCost;
        MaterialButton btnUpdateStock;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartCode = itemView.findViewById(R.id.tvPartCode);
            tvLowStockBadge = itemView.findViewById(R.id.tvLowStockBadge);
            tvPartName = itemView.findViewById(R.id.tvPartName);
            tvCategoryBranch = itemView.findViewById(R.id.tvCategoryBranch);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvMinAlert = itemView.findViewById(R.id.tvMinAlert);
            tvUnitCost = itemView.findViewById(R.id.tvUnitCost);
            btnUpdateStock = itemView.findViewById(R.id.btnUpdateStock);
        }
    }
}
