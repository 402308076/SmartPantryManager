package com.example.smartpantrymanager.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Loads the starter recipe collection (assignment Section 2.2 - at
 * least 15-20 recipes) into the database the very first time the app
 * runs. Only ever called from DatabaseHelper#onCreate.
 */
final class RecipeSeeder {

    private RecipeSeeder() {
    }

    static void seed(SQLiteDatabase db) {
        addRecipe(db, "Tomato Pasta",
                "Boil the spaghetti until al dente. Fry chopped onion and garlic in olive oil, "
                        + "add chopped tomato and simmer for 10 minutes. Toss the pasta through the sauce and serve.",
                new Object[][]{
                        {"spaghetti", 200.0, "g"},
                        {"tomato", 3.0, "pc"},
                        {"garlic", 2.0, "pc"},
                        {"olive oil", 30.0, "ml"},
                        {"onion", 1.0, "pc"}
                });

        addRecipe(db, "Vegetable Fried Rice",
                "Scramble the egg in a hot pan and set aside. Fry diced onion and carrot, add cooked "
                        + "rice, peas and soy sauce, stir through the egg and serve hot.",
                new Object[][]{
                        {"rice", 300.0, "g"},
                        {"egg", 2.0, "pc"},
                        {"carrot", 1.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"soy sauce", 30.0, "ml"},
                        {"peas", 100.0, "g"}
                });

        addRecipe(db, "Cheese Omelette",
                "Whisk the eggs with a splash of milk. Melt the butter in a pan, pour in the egg, "
                        + "sprinkle over the cheese and fold once the base has set.",
                new Object[][]{
                        {"egg", 3.0, "pc"},
                        {"cheese", 50.0, "g"},
                        {"milk", 30.0, "ml"},
                        {"butter", 10.0, "g"}
                });

        addRecipe(db, "Chicken Stir Fry",
                "Slice the chicken breast and stir-fry until browned. Add sliced onion, bell pepper "
                        + "and garlic, then soy sauce, and cook until the vegetables soften.",
                new Object[][]{
                        {"chicken breast", 300.0, "g"},
                        {"bell pepper", 1.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"soy sauce", 30.0, "ml"},
                        {"garlic", 2.0, "pc"}
                });

        addRecipe(db, "Vegetable Soup",
                "Chop the carrot, potato, onion and celery. Simmer everything together in the "
                        + "vegetable stock for 25 minutes until soft, then blend or serve chunky.",
                new Object[][]{
                        {"carrot", 2.0, "pc"},
                        {"potato", 2.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"celery", 1.0, "pc"},
                        {"vegetable stock", 500.0, "ml"}
                });

        addRecipe(db, "Banana Pancakes",
                "Mash the banana and whisk with the egg and milk. Fold in the flour and sugar to a "
                        + "smooth batter, then fry spoonfuls on a hot pan until golden on both sides.",
                new Object[][]{
                        {"banana", 2.0, "pc"},
                        {"flour", 200.0, "g"},
                        {"egg", 1.0, "pc"},
                        {"milk", 200.0, "ml"},
                        {"sugar", 20.0, "g"}
                });

        addRecipe(db, "Peanut Butter Toast",
                "Toast the bread and spread the peanut butter generously over each slice.",
                new Object[][]{
                        {"bread", 2.0, "pc"},
                        {"peanut butter", 30.0, "g"}
                });

        addRecipe(db, "Egg Fried Rice",
                "Scramble the egg and set aside. Fry the cooked rice, stir through the egg, soy "
                        + "sauce and sliced spring onion.",
                new Object[][]{
                        {"rice", 300.0, "g"},
                        {"egg", 2.0, "pc"},
                        {"soy sauce", 20.0, "ml"},
                        {"spring onion", 1.0, "pc"}
                });

        addRecipe(db, "Grilled Cheese Sandwich",
                "Butter the bread, layer the cheese between two slices, and grill in a pan until "
                        + "golden and the cheese has melted.",
                new Object[][]{
                        {"bread", 2.0, "pc"},
                        {"cheese", 2.0, "pc"},
                        {"butter", 10.0, "g"}
                });

        addRecipe(db, "Tuna Salad",
                "Drain the tuna and flake it into a bowl. Mix through the mayonnaise and diced "
                        + "onion, and serve on a bed of lettuce.",
                new Object[][]{
                        {"tuna", 1.0, "pc"},
                        {"mayonnaise", 30.0, "g"},
                        {"onion", 1.0, "pc"},
                        {"lettuce", 50.0, "g"}
                });

        addRecipe(db, "Potato Salad",
                "Boil the potato until tender and cube it. Mix with chopped boiled egg, diced onion "
                        + "and mayonnaise while still slightly warm.",
                new Object[][]{
                        {"potato", 3.0, "pc"},
                        {"mayonnaise", 50.0, "g"},
                        {"egg", 2.0, "pc"},
                        {"onion", 1.0, "pc"}
                });

        addRecipe(db, "Beef Tacos",
                "Brown the beef mince with diced onion. Spoon into taco shells and top with diced "
                        + "tomato and grated cheese.",
                new Object[][]{
                        {"beef mince", 300.0, "g"},
                        {"taco shell", 4.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"tomato", 2.0, "pc"},
                        {"cheese", 50.0, "g"}
                });

        addRecipe(db, "Mushroom Risotto",
                "Fry the onion and garlic, add the rice and toast briefly, then stir in the stock a "
                        + "ladle at a time. Add sliced mushroom and finish with butter.",
                new Object[][]{
                        {"rice", 200.0, "g"},
                        {"mushroom", 200.0, "g"},
                        {"onion", 1.0, "pc"},
                        {"garlic", 2.0, "pc"},
                        {"vegetable stock", 500.0, "ml"},
                        {"butter", 20.0, "g"}
                });

        addRecipe(db, "Chicken Noodle Soup",
                "Simmer the chicken breast in the stock until cooked, shred it, then add the "
                        + "noodles, diced carrot and onion and cook until the noodles are tender.",
                new Object[][]{
                        {"chicken breast", 200.0, "g"},
                        {"noodles", 100.0, "g"},
                        {"carrot", 1.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"vegetable stock", 500.0, "ml"}
                });

        addRecipe(db, "Fruit Smoothie",
                "Blend the banana, milk, yoghurt and honey together until smooth. Serve chilled.",
                new Object[][]{
                        {"banana", 1.0, "pc"},
                        {"milk", 200.0, "ml"},
                        {"yoghurt", 100.0, "g"},
                        {"honey", 20.0, "g"}
                });

        addRecipe(db, "Vegetable Curry",
                "Fry the diced onion, add cubed potato and carrot, curry powder and coconut milk. "
                        + "Simmer until the vegetables are tender.",
                new Object[][]{
                        {"potato", 2.0, "pc"},
                        {"carrot", 1.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"curry powder", 10.0, "g"},
                        {"coconut milk", 200.0, "ml"}
                });

        addRecipe(db, "Bean Chilli",
                "Fry the onion and garlic, add the kidney beans, diced tomato and chilli powder, "
                        + "and simmer for 15 minutes.",
                new Object[][]{
                        {"kidney beans", 400.0, "g"},
                        {"tomato", 3.0, "pc"},
                        {"onion", 1.0, "pc"},
                        {"garlic", 2.0, "pc"},
                        {"chilli powder", 5.0, "g"}
                });

        addRecipe(db, "Garlic Butter Rice",
                "Melt the butter and gently fry the chopped garlic until fragrant. Stir through the "
                        + "cooked rice and chopped parsley.",
                new Object[][]{
                        {"rice", 300.0, "g"},
                        {"garlic", 3.0, "pc"},
                        {"butter", 30.0, "g"},
                        {"parsley", 5.0, "g"}
                });
    }

    private static void addRecipe(SQLiteDatabase db, String name, String instructions, Object[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_RECIPE_NAME, name);
        recipeValues.put(DatabaseHelper.COL_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

        for (Object[] ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            ingredientValues.put(DatabaseHelper.COL_RI_NAME, (String) ingredient[0]);
            ingredientValues.put(DatabaseHelper.COL_RI_QUANTITY, (Double) ingredient[1]);
            ingredientValues.put(DatabaseHelper.COL_RI_UNIT, (String) ingredient[2]);
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
        }
    }
}
