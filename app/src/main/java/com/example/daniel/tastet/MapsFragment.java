package com.example.daniel.tastet;

import android.content.Context;
import android.os.Bundle;
import android.location.Geocoder;
import android.location.Address;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import java.util.List;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class MapsFragment extends Fragment {
    MapView mapView;
    EditText mapSearchBox;
    GoogleMap googleMap;
    Address address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                LatLng ltlng = new LatLng(38.9897, -76.9378);
                float zoom = 15;
                UiSettings uiSetting = map.getUiSettings();
                uiSetting.setZoomControlsEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlng, zoom));
                googleMap = map;
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
                    // hide virtual keyboard
                    try {
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.US);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
                        List<Address> results = geocoder.getFromLocationName(mapSearchBox.getText().toString(), 1);
                        if (results.size() == 0) {
                            return false;
                        }
                        address = results.get(0);
                        Log.d("yeet", mapSearchBox.getText().toString());
                        LatLng ltlng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 15));
                        mapSearchBox.setText("", TextView.BufferType.EDITABLE);
                    } catch (Exception e){
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });


    }
}
