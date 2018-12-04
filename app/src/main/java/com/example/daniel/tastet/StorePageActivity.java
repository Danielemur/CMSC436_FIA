package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
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
                    String reviewTitle = data.getStringExtra(AddReviewActivity.REVIEW_TITLE).toString();
                    String reviewUser = data.getStringExtra(AddReviewActivity.REVIEW_USER).toString();
                    float reviewOverall = data.getFloatExtra(AddReviewActivity.REVIEW_OVERALL, 3);
                    float reviewFreshness = data.getFloatExtra(AddReviewActivity.REVIEW_FRESHNESS, 3);
                    float reviewTaste = data.getFloatExtra(AddReviewActivity.REVIEW_TASTE, 3);
                    float reviewPrice = data.getFloatExtra(AddReviewActivity.REVIEW_PRICE, 3);
                    String reviewText = data.getStringExtra(AddReviewActivity.REVIEW_TEXT).toString();
                    String idOne = UUID.randomUUID().toString();
                    DatabaseReference myRef = database.getReference(idOne);
                    Map<String, Object> result = new HashMap<>();
                    Map<String, String> reviews = new HashMap<>();
                    result.put("Name", reviewTitle);
                    reviews.put("Overall", Float.toString(reviewOverall));
                    reviews.put("Freshness", Float.toString(reviewFreshness));
                    reviews.put("Price", Float.toString(reviewPrice));
                    reviews.put("Taste", Float.toString(reviewTaste));
                    reviews.put("Author", reviewUser);
                    reviews.put("Body", reviewText);
                    result.put("Reviews", reviews);
                    myRef.setValue(result);
                    break;
                }
            }

        }
    }

}
