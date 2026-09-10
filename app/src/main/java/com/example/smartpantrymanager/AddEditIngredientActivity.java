package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

/**
 * Add/Edit screen (assignment Sections 2.2 and 3.1). Used for both
 * creating a new pantry item and editing an existing one - the mode
 * is decided by whether EXTRA_ITEM_ID was passed in via Intent.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    private static final long NO_ID = -1;

    private DatabaseHelper databaseHelper;
    private long editingItemId = NO_ID;

    private EditText inputName;
    private EditText inputQuantity;
    private EditText inputUnit;
    private EditText inputExpiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        inputName = findViewById(R.id.input_name);
        inputQuantity = findViewById(R.id.input_quantity);
        inputUnit = findViewById(R.id.input_unit);
        inputExpiry = findViewById(R.id.input_expiry);
        inputExpiry.setOnClickListener(v -> showDatePicker());

        MaterialButton saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> save());

        MaterialButton deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(v -> delete());

        editingItemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);
        if (editingItemId != NO_ID) {
            loadExistingItem(deleteButton);
        }
    }

    private void loadExistingItem(MaterialButton deleteButton) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_edit_item);
        }
        deleteButton.setVisibility(View.VISIBLE);

        PantryItem item = databaseHelper.getPantryItemById(editingItemId);
        if (item != null) {
            inputName.setText(item.getName());
            inputQuantity.setText(String.valueOf(item.getQuantity()));
            inputUnit.setText(item.getUnit());
            inputExpiry.setText(item.getExpiryDate());
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            inputExpiry.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * Input validation (assignment Section 3.1): every field is
     * checked before anything is written to the database, with a
     * clear inline error shown next to the offending field.
     */
    private void save() {
        String name = inputName.getText().toString().trim();
        String unit = inputUnit.getText().toString().trim();
        String quantityText = inputQuantity.getText().toString().trim();
        String expiry = inputExpiry.getText().toString().trim();

        boolean valid = true;

        if (name.isEmpty()) {
            inputName.setError(getString(R.string.error_name_required));
            valid = false;
        }

        double quantity = 0;
        try {
            quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                inputQuantity.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        } catch (NumberFormatException e) {
            inputQuantity.setError(getString(R.string.error_quantity_invalid));
            valid = false;
        }

        if (unit.isEmpty()) {
            inputUnit.setError(getString(R.string.error_unit_required));
            valid = false;
        }

        if (!valid) {
            return;
        }

        PantryItem item = new PantryItem(editingItemId == NO_ID ? 0 : editingItemId, name, quantity, unit,
                expiry.isEmpty() ? null : expiry);

        if (editingItemId == NO_ID) {
            databaseHelper.addPantryItem(item);
        } else {
            databaseHelper.updatePantryItem(item);
        }

        finish();
    }

    private void delete() {
        if (editingItemId != NO_ID) {
            databaseHelper.deletePantryItem(editingItemId);
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
