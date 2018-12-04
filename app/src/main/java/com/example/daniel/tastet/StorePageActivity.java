package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorePageActivity extends Activity {
    private static final int ADD_REVIEW_REQUEST = 1;
    private Store data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.store_page_activity);

        data = new Store(this.getIntent());


        TextView storeNameView = this.findViewById(R.id.store_page_name);
        TextView storeAddressView = this.findViewById(R.id.store_page_address);
        TextView storeTypeView = this.findViewById(R.id.store_page_type);
        RatingBar storeOverallRatingView = this.findViewById(R.id.store_page_overall_rating);
        RatingBar storeFreshnessRatingView = this.findViewById(R.id.store_page_freshness_rating);
        RatingBar storeTasteRatingView = this.findViewById(R.id.store_page_taste_rating);
        RatingBar storePriceRatingView = this.findViewById(R.id.store_page_price_rating);

        storeNameView.setText(data.getLocationName());
        storeAddressView.setText(data.getLocationAddress());
        storeTypeView.setText(data.getLocationType());
        storeOverallRatingView.setRating(data.getOverallRating());
        storeFreshnessRatingView.setRating(data.getFreshnessRating());
        storeTasteRatingView.setRating(data.getTasteRating());
        storePriceRatingView.setRating(data.getPriceRating());

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
                    final String reviewTitle = data.getStringExtra(AddReviewActivity.REVIEW_TITLE).toString();
                    final String reviewUser = data.getStringExtra(AddReviewActivity.REVIEW_USER).toString();
                    final float reviewOverall = data.getFloatExtra(AddReviewActivity.REVIEW_OVERALL, 3);
                    final float reviewFreshness = data.getFloatExtra(AddReviewActivity.REVIEW_FRESHNESS, 3);
                    final float reviewTaste = data.getFloatExtra(AddReviewActivity.REVIEW_TASTE, 3);
                    final float reviewPrice = data.getFloatExtra(AddReviewActivity.REVIEW_PRICE, 3);
                    final String reviewText = data.getStringExtra(AddReviewActivity.REVIEW_TEXT).toString();

                    String idOne = StorePageActivity.this.data.getHashKey();
                    final DatabaseReference myRef = database.getReference(idOne);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(DataSnapshot snapshot) {
                                                                 Map<String, Object> currentStore = (Map<String, Object>) snapshot.getValue();
                                                                 ArrayList<HashMap<String,Object>> reviews = (ArrayList<HashMap<String,Object>>) currentStore.get("Reviews");
                                                                 HashMap<String,Object> newReview = new HashMap<String,Object>();

                                                                 newReview.put("Title",reviewTitle);
                                                                 newReview.put("Name",reviewUser);
                                                                 newReview.put("Overall",reviewOverall);
                                                                 newReview.put("Freshness",reviewFreshness);
                                                                 newReview.put("Taste",reviewTaste);
                                                                 newReview.put("Price",reviewPrice);
                                                                 newReview.put("Body",reviewText);
                                                                 newReview.put("Date",new java.util.Date().toString());

                                                                 reviews.add(newReview);
                                                                 currentStore.put("Reviews", reviews);
                                                                 myRef.setValue(currentStore);
                                                             }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });

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
