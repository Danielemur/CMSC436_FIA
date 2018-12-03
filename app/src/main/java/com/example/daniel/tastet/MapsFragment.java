package com.example.daniel.tastet;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.location.Geocoder;
import android.location.Address;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.*;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

import java.util.List;

import android.view.LayoutInflater;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.location.*;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class MapsFragment extends Fragment {
    final int PERMISSIONS_REQUEST = 1;
    SharedPreferences sharedPref;
    MapView mapView;
    EditText mapSearchBox;
    GoogleMap googleMap;
    String streetAdd;
    Address address;
    float zoom = 15;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        zoom = Float.parseFloat(sharedPref.getString("default_zoom_key", "15"));
        streetAdd = sharedPref.getString("default_location_key", "");

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location;
                // Default Address if invalid submissions
                LatLng ltlng = new LatLng(38.9897, -76.9378);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference polls = database.getReference();


                if (streetAdd.equals("") || streetAdd.equals("Current Location")) {
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getContext(),
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        } else{
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

                googleMap = map;
                UiSettings uiSetting = googleMap.getUiSettings();
                uiSetting.setZoomControlsEnabled(true);
                polls.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
                        for (DataSnapshot child : snapshot.getChildren()) {
                            try {
                                Map<String, Object> hash = (Map<String, Object>) child.getValue();
                                if (hash.containsKey("Address")) {
                                    List<Address> results = geocoder.getFromLocationName((String) hash.get("Address"), 1);
                                    if (results.size() != 0) {
                                        address = results.get(0);
                                        LatLng lt_lng = new LatLng(address.getLatitude(), address.getLongitude());
                                        googleMap.addMarker(new MarkerOptions()
                                                .position(lt_lng)
                                                .title((String) hash.get("Name")));
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlng, zoom));
            }
        });

        mapSearchBox = (EditText) view.findViewById(R.id.search);
        mapSearchBox.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    try {
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
                        List<Address> results = geocoder.getFromLocationName(mapSearchBox.getText().toString(), 1);
                        if (results.size() == 0) {
                            return false;
                        }
                        address = results.get(0);
                        LatLng ltlng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 15));
                        mapSearchBox.setText("", TextView.BufferType.EDITABLE);
                    } catch (Exception e) {
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
