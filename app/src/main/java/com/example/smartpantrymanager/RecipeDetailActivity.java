package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

/**
 * Shows the full ingredient list and method for one recipe
 * (assignment Section 2.2). The recipe id arrives via Intent from
 * the Suggested Recipes screen.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_recipe_detail);
        }

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        TextView title = findViewById(R.id.text_recipe_title);
        TextView ingredientsView = findViewById(R.id.text_recipe_ingredients);
        TextView methodView = findViewById(R.id.text_recipe_method);

        if (recipe != null) {
            title.setText(recipe.getName());

            StringBuilder ingredientsText = new StringBuilder();
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredientsText.append(String.format("• %s %s %s\n",
                        formatQuantity(ingredient.getQuantity()), ingredient.getUnit(), ingredient.getName()));
            }
            ingredientsView.setText(ingredientsText.toString().trim());
            methodView.setText(recipe.getInstructions());
        }
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
