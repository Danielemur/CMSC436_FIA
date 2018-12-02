package com.example.daniel.tastet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

public class MapsFragment extends Fragment {
    MapView mapView;
    GoogleMap googleMap;

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
                float zoom = 10;
                UiSettings uiSetting = map.getUiSettings();
                uiSetting.setZoomControlsEnabled(true);
                uiSetting.setCompassEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlng, zoom));
                googleMap = map;
            }
        });

    }
}
