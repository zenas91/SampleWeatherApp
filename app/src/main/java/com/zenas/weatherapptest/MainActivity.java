package com.zenas.weatherapptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private int item_selected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLocationButtonClick(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_method_menu, menu);
        MenuItem currentLocation = menu.findItem(R.id.currentLocation);
        MenuItem locationBerlin = menu.findItem(R.id.locationBerlin);
        MenuItem locationStuttgart = menu.findItem(R.id.locationStuttgart);
        MenuItem locationFrankfurt = menu.findItem(R.id.locationFrankfurt);
        if (item_selected == 1) {
            currentLocation.setChecked(true);
        } else if (item_selected == 2) {
            locationBerlin.setChecked(true);
        } else if (item_selected == 3) {
            locationStuttgart.setChecked(true);
        } else if (item_selected == 4) {
            locationFrankfurt.setChecked(true);
        }

    }
}
