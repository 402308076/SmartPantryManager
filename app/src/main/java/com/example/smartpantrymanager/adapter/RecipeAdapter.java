package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;
import java.util.Locale;

/**
 * Displays one recipe row on the Suggested Recipes screen, used for
 * both the strict-match list and the "Almost There" bonus list.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClick {
        void onClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClick onRecipeClick;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClick onRecipeClick) {
        this.recipes = recipes;
        this.onRecipeClick = onRecipeClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.getName());
        holder.subtitle.setText(String.format(Locale.getDefault(), "%d ingredients", recipe.getIngredients().size()));
        holder.itemView.setOnClickListener(v -> onRecipeClick.onClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView subtitle;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_recipe_name);
            subtitle = itemView.findViewById(R.id.text_recipe_subtitle);
        }
    }
}
