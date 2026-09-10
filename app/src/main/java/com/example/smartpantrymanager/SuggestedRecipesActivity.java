package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.logic.MatchingEngine;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Runs the strict-matching rule (assignment Section 2.3) against the
 * user's current pantry and shows only the recipes they can make
 * right now, plus a separate bonus "Almost There" list.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setup(this, bottomNavigationView, R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runMatching(); // Pantry may have changed since we last showed this screen.
    }

    private void runMatching() {
        List<PantryItem> pantry = databaseHelper.getAllPantryItems();
        List<Recipe> allRecipes = databaseHelper.getAllRecipes();

        List<Recipe> suggested = MatchingEngine.getStrictMatches(allRecipes, pantry);
        List<Recipe> almostThere = MatchingEngine.getAlmostThereMatches(allRecipes, pantry);

        RecyclerView suggestedRecycler = findViewById(R.id.recycler_suggested);
        suggestedRecycler.setLayoutManager(new LinearLayoutManager(this));
        suggestedRecycler.setAdapter(new RecipeAdapter(suggested, this::openRecipe));

        RecyclerView almostThereRecycler = findViewById(R.id.recycler_almost_there);
        almostThereRecycler.setLayoutManager(new LinearLayoutManager(this));
        almostThereRecycler.setAdapter(new RecipeAdapter(almostThere, this::openRecipe));

        TextView emptySuggested = findViewById(R.id.text_empty_suggestions);
        emptySuggested.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);

        TextView emptyAlmostThere = findViewById(R.id.text_empty_almost_there);
        emptyAlmostThere.setVisibility(almostThere.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openRecipe(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
