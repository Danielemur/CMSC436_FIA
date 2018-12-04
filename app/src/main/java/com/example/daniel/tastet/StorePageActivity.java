package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class StorePageActivity extends Activity {
    private static final int ADD_REVIEW_REQUEST = 1;
    private Store data;
    ArrayList<Review> list_of_reviews = new ArrayList<Review>();
    CustomAdapter customAdapter = new CustomAdapter();
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

        Button newReviewButton = this.findViewById(R.id.review_button);
        newReviewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addReview = new Intent(StorePageActivity.this, AddReviewActivity.class);
                startActivityForResult(addReview, ADD_REVIEW_REQUEST);
            }
        });
        String idOne = StorePageActivity.this.data.getHashKey();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference polls = database.getReference(idOne);
        polls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {}
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear any existing data
                list_of_reviews.clear();
                customAdapter.notifyDataSetChanged();

                Map<String, Object> store = (Map<String, Object>) snapshot.getValue();

                String storeName =  (String)store.get("Name");

                if(store.containsKey("Reviews")) {
                    List<Map<String, Object>> reviews = (List<Map<String, Object>>) store.get("Reviews");

                    for (Map<String, Object> review : reviews) {


                        String title = review.get("Title").toString();
                        String user = review.get("Name").toString();
                        float overall = Float.parseFloat(review.get("Overall").toString());
                        float freshness = Float.parseFloat(review.get("Freshness").toString());
                        float taste = Float.parseFloat(review.get("Taste").toString());
                        float price = Float.parseFloat(review.get("Price").toString());
                        String text = review.get("Body").toString();

                        String pattern = "EEE MMM dd HH:mm:ss zzz yyyy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
                        Date date;
                        try {
                            date = dateFormat.parse((String) review.get("Date"));
                        } catch (ParseException e) {
                            date = new Date();
                        }

                        Review reviewObj = new Review(title, user, overall, freshness, taste, price, text, storeName, date);

                        list_of_reviews.add(reviewObj);
                    }
                }
                customAdapter.notifyDataSetChanged();
            }
        });
        ListView listView = findViewById(R.id.store_list_of_items);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Review data = (Review) adapterView.getItemAtPosition(i);
                Intent storePage = new Intent(StorePageActivity.this, ReviewPageActivity.class);
                data.packageIntent(storePage);
                startActivity(storePage);
            }
        });
    }



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

                    break;
                }
            }

        }
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_of_reviews.size();
        }

        @Override
        public Object getItem(int i) {
            return list_of_reviews.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Collections.sort(list_of_reviews);
            view = getLayoutInflater().inflate(R.layout.single_review,null);

            Review thisReview = list_of_reviews.get(i);
            TextView reviewBoldTitleTextView = view.findViewById(R.id.singleReviewBoldTitle);

            TextView reviewTextView = view.findViewById(R.id.singleReview);
            AppCompatRatingBar ratingBar = view.findViewById(R.id.ratingBar);
            ratingBar.setRating(thisReview.getOverallRating());
            //ratingBar.setEnabled(false);
            reviewBoldTitleTextView.setText(thisReview.getStoreName());
            //reviewTextView.setText(thisReview.getDisplayBody());
            reviewTextView.setText(thisReview.getSmallBody());
            return view;
        }
    }
}
