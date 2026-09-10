package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Settings screen (assignment Section 2.2 - required to satisfy the
 * minimum-screens rule in Section 3.1). Preferences are stored in
 * SharedPreferences rather than the SQLite database, since they are
 * simple app-wide flags rather than pantry/recipe data.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_UNITS_METRIC = "units_metric";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SwitchMaterial expiryAlertsSwitch = findViewById(R.id.switch_expiry_alerts);
        expiryAlertsSwitch.setChecked(preferences.getBoolean(KEY_EXPIRY_ALERTS, true));
        expiryAlertsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            Toast.makeText(this, R.string.btn_save, Toast.LENGTH_SHORT).show();
        });

        RadioGroup unitsGroup = findViewById(R.id.radio_units);
        boolean metric = preferences.getBoolean(KEY_UNITS_METRIC, true);
        unitsGroup.check(metric ? R.id.radio_metric : R.id.radio_imperial);
        unitsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            preferences.edit().putBoolean(KEY_UNITS_METRIC, checkedId == R.id.radio_metric).apply();
            Toast.makeText(this, R.string.btn_save, Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationHelper.setup(this, bottomNavigationView, R.id.nav_settings);
    }
}
