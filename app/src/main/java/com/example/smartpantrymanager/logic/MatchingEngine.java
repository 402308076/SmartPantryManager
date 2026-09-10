package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The core business rule of the app (assignment brief, Section 2.3):
 * a recipe may only be "suggested" if every single ingredient it
 * requires is present in the user's pantry, in at least the required
 * quantity. There is no partial credit - one missing or insufficient
 * ingredient disqualifies the whole recipe from the main suggestions
 * list.
 * <p>
 * To stay robust against simple real-world messiness (plurals, unit
 * synonyms, case/whitespace) without needing a full NLP solution,
 * ingredient names and units are normalised to a canonical form
 * before anything is compared.
 */
public final class MatchingEngine {

    private MatchingEngine() {
        // Static utility class - never instantiated.
    }

    /** Result of comparing one recipe against the pantry. */
    public static class MatchResult {
        public final Recipe recipe;
        public final List<String> missingIngredients;

        MatchResult(Recipe recipe, List<String> missingIngredients) {
            this.recipe = recipe;
            this.missingIngredients = missingIngredients;
        }

        public boolean isFullMatch() {
            return missingIngredients.isEmpty();
        }
    }

    /** Recipes where every required ingredient is fully satisfied. */
    public static List<Recipe> getStrictMatches(List<Recipe> allRecipes, List<PantryItem> pantry) {
        List<Recipe> matches = new ArrayList<>();
        for (MatchResult result : evaluateAll(allRecipes, pantry)) {
            if (result.isFullMatch()) {
                matches.add(result.recipe);
            }
        }
        return matches;
    }

    /**
     * Bonus "Almost There" list (Section 8): recipes missing exactly
     * one ingredient. Deliberately kept separate from the strict
     * suggestions above - a recipe missing even one ingredient must
     * never appear as "suggested".
     */
    public static List<Recipe> getAlmostThereMatches(List<Recipe> allRecipes, List<PantryItem> pantry) {
        List<Recipe> almostThere = new ArrayList<>();
        for (MatchResult result : evaluateAll(allRecipes, pantry)) {
            if (!result.isFullMatch() && result.missingIngredients.size() == 1) {
                almostThere.add(result.recipe);
            }
        }
        return almostThere;
    }

    /** Evaluates every recipe against the pantry once, returning full detail for each. */
    public static List<MatchResult> evaluateAll(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, Map<String, Double>> pantryTotals = buildPantryTotals(pantry);

        List<MatchResult> results = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            List<String> missing = new ArrayList<>();
            for (RecipeIngredient required : recipe.getIngredients()) {
                if (!isSatisfied(required, pantryTotals)) {
                    missing.add(required.getName());
                }
            }
            results.add(new MatchResult(recipe, missing));
        }
        return results;
    }

    /**
     * Checks whether the pantry holds enough of one required
     * ingredient, after converting both sides to the same canonical
     * unit family (weight, volume, spoon, or count).
     */
    private static boolean isSatisfied(RecipeIngredient required, Map<String, Map<String, Double>> pantryTotals) {
        String normalisedName = normaliseName(required.getName());
        Map<String, Double> availableByFamily = pantryTotals.get(normalisedName);
        if (availableByFamily == null) {
            return false; // Ingredient not in the pantry at all.
        }

        String family = unitFamily(required.getUnit());
        Double availableAmount = availableByFamily.get(family);
        if (availableAmount == null) {
            return false; // Have the ingredient, but not in a comparable unit.
        }

        double requiredAmount = toCanonicalAmount(required.getQuantity(), required.getUnit());
        return availableAmount >= requiredAmount - 0.0001; // tolerate floating point rounding
    }

    /**
     * Groups pantry items by normalised ingredient name and unit
     * family, converting every quantity to its family's canonical
     * unit (grams for weight, millilitres for volume, teaspoons for
     * spoon measures, whole pieces for count) so quantities can be
     * safely summed and compared.
     */
    private static Map<String, Map<String, Double>> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Map<String, Double>> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String name = normaliseName(item.getName());
            String family = unitFamily(item.getUnit());
            double canonicalAmount = toCanonicalAmount(item.getQuantity(), item.getUnit());

            Map<String, Double> byFamily = totals.computeIfAbsent(name, key -> new HashMap<>());
            byFamily.merge(family, canonicalAmount, Double::sum);
        }
        return totals;
    }

    /**
     * Naive singular/plural + whitespace/case normalisation for
     * ingredient names, e.g. "Tomatoes" and "tomato " both become
     * "tomato". Intentionally simple (per Section 2.3) rather than a
     * full NLP/stemming solution.
     */
    public static String normaliseName(String rawName) {
        String name = rawName.trim().toLowerCase(Locale.ROOT);
        if (name.endsWith("ies") && name.length() > 3) {
            name = name.substring(0, name.length() - 3) + "y";   // "berries" -> "berry"
        } else if (name.endsWith("oes") && name.length() > 3) {
            name = name.substring(0, name.length() - 2);         // "tomatoes" -> "tomato"
        } else if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 1) {
            name = name.substring(0, name.length() - 1);         // "onions" -> "onion"
        }
        return name;
    }

    /** Maps a raw unit string to one of four comparable families. */
    private static String unitFamily(String rawUnit) {
        String unit = rawUnit.trim().toLowerCase(Locale.ROOT);
        switch (unit) {
            case "g":
            case "gram":
            case "grams":
            case "kg":
            case "kilogram":
            case "kilograms":
                return "weight";
            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "volume";
            case "tsp":
            case "teaspoon":
            case "teaspoons":
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "spoon";
            default:
                return "count"; // pc, pcs, piece, pieces, whole, or anything unrecognised
        }
    }

    /** Converts a quantity into its family's canonical base unit. */
    private static double toCanonicalAmount(double quantity, String rawUnit) {
        String unit = rawUnit.trim().toLowerCase(Locale.ROOT);
        switch (unit) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000.0; // canonical weight unit is grams
            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity * 1000.0; // canonical volume unit is millilitres
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return quantity * 3.0;    // canonical spoon unit is teaspoons
            default:
                return quantity;
        }
    }
}
