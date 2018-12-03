package com.example.daniel.tastet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class NavigatorActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        switch(item.getItemId()){
            case R.id.add_location:
                String idOne = UUID.randomUUID().toString();
                DatabaseReference myRef = database.getReference(idOne);
                HashMap<String, Object> result = new HashMap<>();
                result.put("Name", "CVS");
                result.put("Address", "College Park, MD");
                result.put("Store Type", "Convenience Store");

                myRef.setValue(result);
                break;
            case R.id.add_review:
                DatabaseReference myRef2 = database.getReference("Location");
                myRef2.setValue("Hello, World! Add Review");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        //initialize to home when just starting up
        loadFragment(new HomeFragment());

        //initialize toolbar called appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

    }
    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
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
                fragment = new HomeFragment();
                break;
            case R.id.navigation_search:
                fragment = new SearchFragment();
                break;
            case R.id.navigation_map:
                fragment = new MapsFragment();
                break;
            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;
        }
        return loadFragment(fragment);
    }
}
