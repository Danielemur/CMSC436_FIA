package com.example.daniel.tastet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsFragment extends PreferenceFragmentCompat {
  /*  @Nullable
    @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment,null);
    }*/


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //Load preferences from XML file
        addPreferencesFromResource(R.xml.preferences);
/*
        //Preference mNotificationPreference = getPreferenceManager().findPreference("notification_key");
        //Preference mDefaultLocation = getPreferenceManager().findPreference("default_location_key");
        //Preference mDefaultZoom = getPreferenceManager().findPreference("zoom_key");



        SharedPreferences prefs = getActivity().getSharedPreferences("SettingsFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        *//*If the default location hasn't been set, set it*//*
        if (!prefs.getString("default_location_key", "missing").equals("missing")) {
            *//*Add default location to SettingsFile*//*
            editor.putString("default_location_key", "home");
            editor.apply();
        }

        *//*If the default zoom setting hasn't been set, set it*//*
        if (!prefs.getString("default_zoom_key", "missing").equals("missing")) {
            *//*Add default zoom level for map to SettingsFile*//*
            editor.putInt("default_zoom_preference", 10);
            editor.apply();
        }

        *//*If the default zoom setting hasn't been set, set it*//*
        if (!prefs.getString("notifications_key", "missing").equals("missing")) {
            *//*Add default notification setting to SettingsFile*//*
            editor.putBoolean("notification_key", true);
            editor.apply();
        }*/



    }








}
