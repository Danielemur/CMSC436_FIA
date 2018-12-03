package com.example.daniel.tastet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class NavigatorActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = "TasteT";
    private static final int ADD_STORE_REQUEST = 0;


    private Fragment currentFragment = null;
    private Fragment homeFragment = null;
    private Fragment searchFragment = null;
    private Fragment mapsFragment = null;
    private Fragment settingsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);


        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        mapsFragment = new MapsFragment();
        settingsFragment = new SettingsFragment();

        //initialize to home when just starting up
        currentFragment = homeFragment;
        loadFragment();

        //initialize toolbar called appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_black_24dp));
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO
        //add the two options addlocation and addreview
        switch(item.getItemId()){
            case R.id.add_location:
                Intent addStore = new Intent(this, AddStoreActivity.class);
                this.startActivityForResult(addStore, ADD_STORE_REQUEST);
                //open new location activity
                break;
            case R.id.add_review:
                //open new review activity
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Entered onActivityResult()");
        if (resultCode == RESULT_OK && requestCode == ADD_STORE_REQUEST) {
            String locationName = data.getStringExtra(AddStoreActivity.LOCATION_NAME);
            String locationAddress = data.getStringExtra(AddStoreActivity.LOCATION_ADDRESS);
            String locationType = data.getStringExtra(AddStoreActivity.LOCATION_TYPE);
            Log.i(TAG, "Name: " + locationName + "\nAddress: " + locationAddress + "\nType: " + locationType);
        }
    }

    private boolean loadFragment(){
        if(currentFragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
            return true;
        }
        return false;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        //switch to the selected menu option
        switch(menuItem.getItemId()){
            case R.id.navigation_home:
                currentFragment = homeFragment;
                break;
            case R.id.navigation_search:
                currentFragment = searchFragment;
                break;
            case R.id.navigation_map:
                currentFragment = mapsFragment;
                break;
            case R.id.navigation_settings:
                currentFragment = settingsFragment;
                break;
        }
        return loadFragment();
    }
}
