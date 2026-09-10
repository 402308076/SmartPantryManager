package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Wires up the shared bottom navigation bar (assignment Section 3.1
 * - "a working navigation element") so any of the three main screens
 * (Pantry, Recipes, Settings) can jump straight to another.
 */
final class NavigationHelper {

    private NavigationHelper() {
    }

    static void setup(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == selectedItemId) {
                return true;
            }
            Class<?> destination;
            if (item.getItemId() == R.id.nav_pantry) {
                destination = PantryListActivity.class;
            } else if (item.getItemId() == R.id.nav_recipes) {
                destination = SuggestedRecipesActivity.class;
            } else {
                destination = SettingsActivity.class;
            }
            Intent intent = new Intent(activity, destination);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(intent);
            activity.finish();
            return true;
        });
    }
}
