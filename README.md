# Smart Pantry Manager

A Java Android application that helps reduce food waste by tracking the ingredients a user actually has at home (their "pantry") and suggesting recipes that can be made **strictly** from what's already on hand.

## Database choice: SQLite (SQLiteOpenHelper)

SQLite was chosen because the app's data (pantry items and recipes) is inherently per-device and doesn't need to sync anywhere. A local, offline-first database avoids any network dependency while demoing/grading, and matches the `SQLiteOpenHelper` approach covered in the module's persistent-data chapter. A plain `SQLiteOpenHelper` (rather than the Room library) was used deliberately, to keep the SQL schema and every CRUD operation fully explicit and easy to walk through in the video demonstration and written report.

## Setup / run instructions

1. Clone this repository.
2. Open the project folder in Android Studio (**File > Open**).
3. Let Gradle sync (Android Studio will prompt to download any missing SDK/build tools).
4. Run the app on an emulator or physical device running Android 8.0 (API 26) or later.
5. On first launch the app seeds its own recipe collection automatically (18 recipes) - no manual setup needed.

## Project structure

- `model/` - `PantryItem`, `Recipe`, `RecipeIngredient` (plain data classes)
- `data/` - `DatabaseHelper` (SQLite schema + CRUD) and `RecipeSeeder` (first-run recipe data)
- `logic/` - `MatchingEngine` (the strict-matching business rule - the core of the app)
- `adapter/` - `PantryAdapter`, `RecipeAdapter` (RecyclerView adapters)
- Activities: `PantryListActivity`, `AddEditIngredientActivity`, `SuggestedRecipesActivity`, `RecipeDetailActivity`, `SettingsActivity`
