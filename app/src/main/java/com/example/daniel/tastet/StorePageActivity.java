package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class StorePageActivity extends Activity {
    private static final int ADD_REVIEW_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


  /*          case R.id.add_review:
    Intent addReview = new Intent(this, AddReviewActivity.class);
                this.startActivityForResult(addReview, ADD_REVIEW_REQUEST);
    //open new location activity
                break;*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(NavigatorActivity.TAG, "Entered onActivityResult()");
        if (resultCode == RESULT_OK) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            switch(requestCode) {
                case ADD_REVIEW_REQUEST: {
                    String locationName = data.getStringExtra(AddStoreActivity.LOCATION_NAME);
                    String locationAddress = data.getStringExtra(AddStoreActivity.LOCATION_ADDRESS);
                    String locationType = data.getStringExtra(AddStoreActivity.LOCATION_TYPE);
                    Log.i(NavigatorActivity.TAG, "Name: " + locationName + "\nAddress: " + locationAddress + "\nType: " + locationType);


                    String idOne = UUID.randomUUID().toString();
                    DatabaseReference myRef = database.getReference(idOne);
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("Name", "CVS");
                    result.put("Address", "College Park, MD");
                    result.put("Store Type", "Convenience Store");

                    myRef.setValue(result);



                    DatabaseReference myRef2 = database.getReference("Location");
                    myRef2.setValue("Hello, World! Add Review");
                }
            }

        }
    }

}
