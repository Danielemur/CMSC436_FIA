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

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

import java.util.List;

import android.view.LayoutInflater;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
    AutoCompleteTextView mapSearchBox;
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
        zoom = Float.parseFloat(sharedPref.getString(NavigatorActivity.DEFAULT_ZOOM_KEY, "15"));
        streetAdd = sharedPref.getString(NavigatorActivity.DEFAULT_LOCATION_KEY, "");

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location;
                // Default Address if invalid submissions
                LatLng ltlng = NavigatorActivity.LAT_LNG_CP;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference polls = database.getReference();

                if (streetAdd.equals("") || streetAdd.equals(NavigatorActivity.CURRENT_LOCATION_KEY)) {
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getContext(),
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // No permission for current location
                        streetAdd = NavigatorActivity.COLLEGE_PARK_ADDRESS;
                        try {
                            List<Address> results = geocoder.getFromLocationName(streetAdd, 1);
                            if (results.size() != 0) {
                                address = results.get(0);
                                ltlng = new LatLng(address.getLatitude(), address.getLongitude());
                            }
                        } catch (Exception e) {
                            //Invalid Address - Default to CP, MD
                            ltlng = NavigatorActivity.LAT_LNG_CP;
                        }
                    } else {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location == null) {
                            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        if (location != null) {
                            ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                        } else{
                            ltlng = NavigatorActivity.LAT_LNG_CP;
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
                        ltlng = NavigatorActivity.LAT_LNG_CP;
                    }
                }

                googleMap = map;
                UiSettings uiSetting = googleMap.getUiSettings();
                uiSetting.setZoomControlsEnabled(true);
                polls.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            try {
                                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
                                Map<String, Object> hash = (Map<String, Object>) child.getValue();
                                if (hash.containsKey(NavigatorActivity.ADDRESS_KEY)) {
                                    List<Address> results = geocoder.getFromLocationName((String) hash.get(NavigatorActivity.ADDRESS_KEY), 1);
                                    if (results.size() != 0) {
                                        address = results.get(0);
                                        LatLng lt_lng = new LatLng(address.getLatitude(), address.getLongitude());
                                        googleMap.addMarker(new MarkerOptions()
                                                .position(lt_lng)
                                                .title((String) hash.get(NavigatorActivity.NAME_KEY)));
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

        mapSearchBox = (AutoCompleteTextView) view.findViewById(R.id.search);

        AddressAutocompleteAdapter addressAdapter = new AddressAutocompleteAdapter(this.getContext());
        mapSearchBox.setAdapter(addressAdapter);
        mapSearchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                String address = ((AddressAutocompleteAdapter.Place) adapterView.getItemAtPosition(i))
                        .getAddress();
                mapSearchBox.setText(address);
            }
        });

        mapSearchBox.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO || (event != null &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
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
    public void onStop(){
        mapView.onStop();
        super.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
}
