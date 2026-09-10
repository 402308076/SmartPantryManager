package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Binds the user's pantry items into the RecyclerView on
 * PantryListActivity (assignment Section 3.1 - RecyclerView + custom
 * Adapter requirement).
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Simple callback so the Activity decides what happens on tap/delete. */
    public interface OnItemClick {
        void onClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClick onItemClick;
    private final OnItemClick onDeleteClick;

    public PantryAdapter(OnItemClick onItemClick, OnItemClick onDeleteClick) {
        this.onItemClick = onItemClick;
        this.onDeleteClick = onDeleteClick;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(String.format(Locale.getDefault(), "%s %s",
                formatQuantity(item.getQuantity()), item.getUnit()));

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.expiry.setText(holder.expiry.getContext().getString(R.string.label_expiry)
                    + ": " + item.getExpiryDate());
            holder.expiry.setVisibility(View.VISIBLE);
        } else {
            holder.expiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> onItemClick.onClick(item));
        holder.deleteButton.setOnClickListener(v -> onDeleteClick.onClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatQuantity(double quantity) {
        // Show whole numbers cleanly (2 not 2.0), but keep decimals when they matter (2.5).
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView expiry;
        final ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_item_name);
            quantity = itemView.findViewById(R.id.text_item_quantity);
            expiry = itemView.findViewById(R.id.text_item_expiry);
            deleteButton = itemView.findViewById(R.id.button_delete_item);
        }
    }
}
