package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Launcher screen (assignment Section 2.2): lists everything
 * currently in the user's pantry, backed by a RecyclerView + custom
 * adapter reading straight from SQLite.
 */
public class PantryListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        databaseHelper = new DatabaseHelper(this);
        emptyView = findViewById(R.id.text_empty_pantry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_pantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this::openEditItem, this::confirmDelete);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_item);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditIngredientActivity.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setup(this, bottomNavigationView, R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList(); // Reload every time we return here (e.g. after adding/editing an item).
    }

    private void refreshList() {
        List<PantryItem> items = databaseHelper.getAllPantryItems();
        adapter.setItems(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openEditItem(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    private void confirmDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, item.getName()))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    databaseHelper.deletePantryItem(item.getId());
                    refreshList();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
