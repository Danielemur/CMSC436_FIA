package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;



public class AddStoreActivity extends Activity {
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String LOCATION_ADDRESS = "LOCATION_ADDRESS";
    public static final String LOCATION_TYPE = "LOCATION_TYPE";
    private GeoDataClient mGeodataClient;
    private AutoCompleteTextView addressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_store_activity);

        mGeodataClient = Places.getGeoDataClient(this);

        addressView = this.findViewById(R.id.add_store_address_entry);
        AddressAutocompleteAdapter addressAdapter = new AddressAutocompleteAdapter(this);
        addressView.setAdapter(addressAdapter);
        addressView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                String address = ((AddressAutocompleteAdapter.Place) adapterView.getItemAtPosition(i))
                        .getAddress();
                addressView.setText(address);
            }
        });
    }

    public void cancel(View v) {
        finish();
    }

    public void submit(View v) {

        // gather data
        EditText storeNameView = this.findViewById(R.id.add_store_name_entry);
        EditText storeAddressView = this.findViewById(R.id.add_store_address_entry);
        Spinner storeTypeView = this.findViewById(R.id.add_store_type_entry);

        Intent intent = new Intent();
        intent.putExtra(LOCATION_NAME, storeNameView.getText());
        intent.putExtra(LOCATION_ADDRESS, storeAddressView.getText());
        intent.putExtra(LOCATION_TYPE, storeTypeView.getSelectedItem().toString());

        // return data Intent and finish
        setResult(RESULT_OK, intent);
        finish();

    }
}
