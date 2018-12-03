package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;


public class AddReviewActivity extends Activity {
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String LOCATION_ADDRESS = "LOCATION_ADDRESS";
    public static final String LOCATION_TYPE = "LOCATION_TYPE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_review_activity);

    }

    public void cancel(View v) {
        finish();
    }

    public void submit(View v) {

        // gather ToDoItem data
/*        EditText storeNameView = this.findViewById(R.id.add_review_name_entry);
        EditText storeAddressView = this.findViewById(R.id.add_review_address_entry);
        Spinner storeTypeView = this.findViewById(R.id.add_review_type_entry);

        Intent intent = new Intent();
        intent.putExtra(LOCATION_NAME, storeNameView.getText());
        intent.putExtra(LOCATION_ADDRESS, storeAddressView.getText());
        intent.putExtra(LOCATION_TYPE, storeTypeView.getSelectedItem().toString());

        // return data Intent and finish
        setResult(RESULT_OK, intent);*/
        finish();

    }
}
