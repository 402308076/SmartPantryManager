package com.example.smartpantrymanager.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for the strict-matching engine (assignment Section 2.3
 * and Section 8's "Almost There" bonus). These run on the plain JVM
 * (no emulator/Android framework needed) since MatchingEngine and
 * the model classes have no Android dependencies - that separation
 * is exactly what makes this logic unit-testable at all.
 * <p>
 * Pantry items use id 0 throughout since the id is irrelevant to
 * matching - only name, quantity and unit are ever read by the
 * engine.
 */
public class MatchingEngineTest {

    // ---- normaliseName() ----------------------------------------

    @Test
    public void normaliseName_lowercasesAndTrimsWhitespace() {
        assertEquals("onion", MatchingEngine.normaliseName("  Onion  "));
    }

    @Test
    public void normaliseName_stripsSimplePlural() {
        assertEquals("egg", MatchingEngine.normaliseName("Eggs"));
    }

    @Test
    public void normaliseName_handlesOesPlural() {
        assertEquals("tomato", MatchingEngine.normaliseName("Tomatoes"));
    }

    @Test
    public void normaliseName_handlesIesPlural() {
        assertEquals("berry", MatchingEngine.normaliseName("berries"));
    }

    @Test
    public void normaliseName_leavesWordsEndingInDoubleSUnchanged() {
        // Naive plural-stripping (per the brief) deliberately does not
        // touch words like "grass" - stripping to "gras" would be wrong.
        assertEquals("grass", MatchingEngine.normaliseName("grass"));
    }

    // ---- strict all-or-nothing matching ---------------------------

    @Test
    public void getStrictMatches_includesRecipeWhenEveryIngredientPresent() {
        Recipe recipe = recipeWith("Egg Fried Rice",
                ingredient("rice", 300, "g"),
                ingredient("egg", 2, "pc"),
                ingredient("soy sauce", 20, "ml"),
                ingredient("spring onion", 1, "pc"));

        List<PantryItem> pantry = pantryOf(
                item("Rice", 300, "g"),
                item("Egg", 2, "pc"),
                item("Soy Sauce", 20, "ml"),
                item("Spring Onion", 1, "pc"));

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertEquals(1, matches.size());
        assertEquals("Egg Fried Rice", matches.get(0).getName());
    }

    @Test
    public void getStrictMatches_excludesRecipeMissingJustOneIngredient() {
        // This is the core business rule from Section 2.3: there is no
        // partial credit. Missing even a single ingredient must
        // disqualify the whole recipe from the strict-match list.
        Recipe recipe = recipeWith("Egg Fried Rice",
                ingredient("rice", 300, "g"),
                ingredient("egg", 2, "pc"),
                ingredient("soy sauce", 20, "ml"),
                ingredient("spring onion", 1, "pc"));

        List<PantryItem> pantry = pantryOf(
                item("Rice", 300, "g"),
                item("Soy Sauce", 20, "ml"),
                item("Spring Onion", 1, "pc"));
        // Egg deliberately left out.

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void getStrictMatches_excludesRecipeWhenQuantityIsInsufficient() {
        Recipe recipe = recipeWith("Vegetable Soup", ingredient("onion", 3, "pc"));
        List<PantryItem> pantry = pantryOf(item("Onion", 2, "pc")); // have 2, need 3

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void getStrictMatches_emptyPantryMatchesNothing() {
        Recipe recipe = recipeWith("Egg Fried Rice", ingredient("rice", 300, "g"));

        List<Recipe> matches = MatchingEngine.getStrictMatches(
                Collections.singletonList(recipe), new ArrayList<>());

        assertTrue(matches.isEmpty());
    }

    // ---- "Almost There" bonus list ---------------------------------

    @Test
    public void getAlmostThereMatches_includesRecipeMissingExactlyOneIngredient() {
        Recipe recipe = recipeWith("Egg Fried Rice",
                ingredient("rice", 300, "g"),
                ingredient("egg", 2, "pc"),
                ingredient("soy sauce", 20, "ml"),
                ingredient("spring onion", 1, "pc"));

        List<PantryItem> pantry = pantryOf(
                item("Rice", 300, "g"),
                item("Soy Sauce", 20, "ml"),
                item("Spring Onion", 1, "pc"));
        // Missing exactly one ingredient: egg.

        List<Recipe> almostThere = MatchingEngine.getAlmostThereMatches(Collections.singletonList(recipe), pantry);

        assertEquals(1, almostThere.size());
    }

    @Test
    public void getAlmostThereMatches_excludesRecipeMissingTwoOrMoreIngredients() {
        Recipe recipe = recipeWith("Egg Fried Rice",
                ingredient("rice", 300, "g"),
                ingredient("egg", 2, "pc"),
                ingredient("soy sauce", 20, "ml"),
                ingredient("spring onion", 1, "pc"));

        List<PantryItem> pantry = pantryOf(item("Rice", 300, "g"));
        // Missing egg, soy sauce and spring onion - three, not one.

        List<Recipe> almostThere = MatchingEngine.getAlmostThereMatches(Collections.singletonList(recipe), pantry);

        assertTrue(almostThere.isEmpty());
    }

    @Test
    public void getAlmostThereMatches_neverIncludesAFullMatch() {
        // A recipe that's already a full match belongs only in
        // getStrictMatches(), never duplicated into "Almost There".
        Recipe recipe = recipeWith("Egg Fried Rice", ingredient("rice", 300, "g"));
        List<PantryItem> pantry = pantryOf(item("Rice", 300, "g"));

        List<Recipe> almostThere = MatchingEngine.getAlmostThereMatches(Collections.singletonList(recipe), pantry);

        assertTrue(almostThere.isEmpty());
    }

    // ---- unit-family conversion and pluralised pantry names --------

    @Test
    public void getStrictMatches_convertsKilogramsToSatisfyGramsRequirement() {
        Recipe recipe = recipeWith("Bread", ingredient("flour", 500, "g"));
        List<PantryItem> pantry = pantryOf(item("Flour", 1, "kg")); // 1 kg = 1000 g, more than enough

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertEquals(1, matches.size());
    }

    @Test
    public void getStrictMatches_convertsTablespoonsToSatisfyTeaspoonsRequirement() {
        Recipe recipe = recipeWith("Dressing", ingredient("olive oil", 6, "tsp"));
        List<PantryItem> pantry = pantryOf(item("Olive Oil", 2, "tbsp")); // 2 tbsp = 6 tsp exactly

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertEquals(1, matches.size());
    }

    @Test
    public void getStrictMatches_doesNotCrossWeightAndVolumeUnitFamilies() {
        // 500 g of milk is not a valid comparison against 500 ml required -
        // weight and volume are different families and must never be
        // treated as interchangeable, even though both are "500".
        Recipe recipe = recipeWith("Pancakes", ingredient("milk", 500, "ml"));
        List<PantryItem> pantry = pantryOf(item("Milk", 500, "g"));

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void getStrictMatches_matchesPluralPantryNameAgainstSingularRecipeName() {
        // Pantry item entered as "Onions" (plural) must still satisfy a
        // recipe that lists "onion" (singular) - this is exactly what
        // normaliseName() exists to handle.
        Recipe recipe = recipeWith("Soup", ingredient("onion", 2, "pc"));
        List<PantryItem> pantry = pantryOf(item("Onions", 3, "pc"));

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertEquals(1, matches.size());
    }

    @Test
    public void getStrictMatches_sumsMultiplePantryEntriesOfSameIngredient() {
        // Two separate pantry rows for the same ingredient (e.g. added
        // on different shopping trips) should combine, not just use one.
        Recipe recipe = recipeWith("Soup", ingredient("onion", 4, "pc"));
        List<PantryItem> pantry = pantryOf(
                item("Onion", 2, "pc"),
                item("onion", 2, "pc"));

        List<Recipe> matches = MatchingEngine.getStrictMatches(Collections.singletonList(recipe), pantry);

        assertEquals(1, matches.size());
    }

    // ---- small test-data builders -----------------------------------

    private static PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(0, name, quantity, unit, null);
    }

    private static List<PantryItem> pantryOf(PantryItem... items) {
        List<PantryItem> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }

    private static RecipeIngredient ingredient(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static Recipe recipeWith(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(0, name, "Test instructions.");
        for (RecipeIngredient ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }
}
