package com.example.daniel.tastet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

import android.location.*;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RemoteViews;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NavigatorActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "TasteT";
    private static final int ADD_STORE_REQUEST = 0;


    private Fragment currentFragment = null;
    private Fragment homeFragment = null;
    private Fragment searchFragment = null;
    private Fragment mapsFragment = null;
    private Fragment settingsFragment = null;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference polls = database.getReference();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        polls.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (sharedPref.getBoolean("notification_key", true) == false){
                    return;
                }
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("UPDATE", "NAAAAAAA");
                    Log.d("New_Update", child.toString());
                    try {
                        String notifString;
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location;
                        Address address;
                        Address strAddress;
                        String storeAddress;
                        LatLng storeLatLng = null;
                        LatLng ltlng = new LatLng(38.9897, -76.9378);
                        String streetAdd = sharedPref.getString("default_location_key", "");
                        String storeName;
                        int numReview = -1;
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);

                        Map<String, Object> hash = (Map<String, Object>) child.getValue();
                        if (hash.containsKey("Name")) {
                            storeName = (String) hash.get("Name");
                        } else {
                            break;
                        }
                        if (hash.containsKey("Address")) {
                            storeAddress = (String) hash.get("Address");
                            try {
                                List<Address> results = geocoder.getFromLocationName(storeAddress, 1);
                                if (results.size() != 0) {
                                    strAddress = results.get(0);
                                    storeLatLng = new LatLng(strAddress.getLatitude(), strAddress.getLongitude());
                                }
                            } catch (Exception e) {
                                break;
                            }
                        } else {
                            break;
                        }
                        Log.d("CHECK", "CHECKING");

                        if (streetAdd.equals("") || streetAdd.equals("Current Location")) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // No permission for current location
                                streetAdd = "College Park, Maryland";
                                try {
                                    List<Address> results = geocoder.getFromLocationName(streetAdd, 1);
                                    if (results.size() != 0) {
                                        address = results.get(0);
                                        ltlng = new LatLng(address.getLatitude(), address.getLongitude());
                                    }
                                } catch (Exception e) {
                                    //Invalid Address - Default to CP, MD
                                    ltlng = new LatLng(38.9897, -76.9378);
                                }
                            } else {
                                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location == null) {
                                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                }
                                if (location != null) {
                                    ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                                } else {
                                    ltlng = new LatLng(38.9897, -76.9378);
                                }
                            }
                        } else {
                            try {
                                List<Address> results = geocoder.getFromLocationName(streetAdd, 1);
                                if (results.size() != 0) {
                                    address = results.get(0);
                                    ltlng = new LatLng(address.getLatitude(), address.getLongitude());
                                }
                            } catch (Exception e) {
                                //Invalid Address - Default to CP, MD
                                ltlng = new LatLng(38.9897, -76.9378);
                            }
                        }
                        Location storeLoc = new Location("");
                        storeLoc.setLatitude(storeLatLng.latitude);
                        storeLoc.setLongitude(storeLatLng.longitude);
                        Location currLoc = new Location("");
                        currLoc.setLatitude(ltlng.latitude);
                        currLoc.setLongitude(ltlng.longitude);

                        float dist = (float) (storeLoc.distanceTo(currLoc) / 1609.344);

                        if (hash.containsKey("Reviews")) {
                            numReview = ((HashMap<Object, Object>[]) hash.get("Reviews")).length;
                        }
                        // If dist < 10 miles, put notification
                        if (dist <= 10) {
                            if (numReview == 0 || numReview == -1) {
                                notifString = "New store " + storeName + " added!";
                                Log.d("STORE", storeName);
                            } else {
                                notifString = "New review for " + storeName + " added!";
                                Log.d("REVIEW", storeName);
                            }

                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            String mChannelID = "first";
                            NotificationChannel mChannel =
                                    new NotificationChannel(mChannelID, "channel", NotificationManager.IMPORTANCE_LOW);
                            mNotificationManager.createNotificationChannel(mChannel);
                            RemoteViews contentView = new RemoteViews(
                                    getPackageName(),
                                    R.layout.new_notification);
                            contentView.setTextViewText(R.id.notification_text, notifString);

                            Notification.Builder notificationBuilder = new Notification.Builder(
                                    getApplicationContext(), mChannelID)
                                    .setTicker("New Notification")
                                    .setSmallIcon(android.R.drawable.alert_dark_frame)
                                    .setAutoCancel(true)
                                    .setCustomContentView(contentView);
                            mNotificationManager.notify(1,
                                    notificationBuilder.build());
                        }
                    } catch (Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void launchAddStoreActivity(MenuItem menuItem) {
        //open new location activity
        Intent addStore = new Intent(this, AddStoreActivity.class);
        this.startActivityForResult(addStore, ADD_STORE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Entered onActivityResult()");
        if (resultCode == RESULT_OK) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            switch (requestCode) {
                case ADD_STORE_REQUEST: {
                    String locationName = data.getStringExtra(AddStoreActivity.LOCATION_NAME);
                    String locationAddress = data.getStringExtra(AddStoreActivity.LOCATION_ADDRESS);
                    String locationType = data.getStringExtra(AddStoreActivity.LOCATION_TYPE);
                    Log.i(TAG, "Name: " + locationName + "\nAddress: " + locationAddress + "\nType: " + locationType);


                    String idOne = UUID.randomUUID().toString();
                    DatabaseReference myRef = database.getReference(idOne);
                    Map<String, Object> result = new HashMap<>();
                    result.put("Name", locationName);
                    result.put("Address", locationAddress);
                    result.put("Store Type", locationType);
                    myRef.setValue(result);
                    break;
                }
            }

        }
    }

    private boolean loadFragment() {
        if (currentFragment != null) {
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
        switch (menuItem.getItemId()) {
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


    private static Preference.OnPreferenceChangeListener changeListener
            = new Preference.OnPreferenceChangeListener() {

        public boolean onPreferenceChange(Preference preference, Object value) {

            /*Changing notifications setting*/
            if (preference.getKey().equals("notification_key")) {

            }
            /*Changing default zoom setting*/
            else if (preference.getKey().equals("default_zoom_key")) {

            }
            /*Changing default location*/
            else if (preference.getKey().equals("default_location_key")) {

            }
            /*Error, something went wrong*/
            else {
                return false;
            }

            return true;
        }


    };


}
