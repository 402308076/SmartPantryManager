package com.example.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Single source of truth for on-device persistence (assignment
 * Section 3.2 - SQLite option). Owns the schema, seeds the recipe
 * collection on first run, and exposes full CRUD for pantry items.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "required_quantity";
    public static final String COL_RI_UNIT = "required_unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL UNIQUE, " +
                COL_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QUANTITY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))");

        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------- Pantry CRUD ----------

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_PANTRY, null, toValues(item));
        db.close();
        return id;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_PANTRY, toValues(item), COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_NAME + " ASC");
        while (cursor.moveToNext()) {
            items.add(readPantryItem(cursor));
        }
        cursor.close();
        db.close();
        return items;
    }

    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = readPantryItem(cursor);
        }
        cursor.close();
        db.close();
        return item;
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return values;
    }

    private PantryItem readPantryItem(Cursor cursor) {
        return new PantryItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));
    }

    // ---------- Recipes (read-only from the app's point of view) ----------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_NAME + " ASC");
        while (cursor.moveToNext()) {
            Recipe recipe = new Recipe(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS)));
            loadIngredients(db, recipe);
            recipes.add(recipe);
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = new Recipe(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS)));
            loadIngredients(db, recipe);
        }
        cursor.close();
        db.close();
        return recipe;
    }

    private void loadIngredients(SQLiteDatabase db, Recipe recipe) {
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipe.getId())}, null, null, COL_RI_ID + " ASC");
        while (cursor.moveToNext()) {
            recipe.addIngredient(new RecipeIngredient(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT))));
        }
        cursor.close();
    }
}
