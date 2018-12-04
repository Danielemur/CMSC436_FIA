package com.example.daniel.tastet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchFragment extends Fragment {
    public static final String TAG = "TasteT";
    EditText searchBox;
    String storeQuery;
    ArrayList<SearchFragment.Store> list_of_stores = new ArrayList<Store>();
    ArrayList<SearchFragment.Store> matchedStores = new ArrayList<Store>();
    SearchFragment.CustomAdapter customAdapter = new SearchFragment.CustomAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //search bar functionality
        searchBox = (EditText) view.findViewById(R.id.searchStore);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    storeQuery = searchBox.getText().toString();
                    Log.i(TAG, "searched for " + storeQuery);
                    searchBox.setText("", TextView.BufferType.EDITABLE);

                    //query based on result
                    matchedStores.clear();
                    for (Store store : list_of_stores) {

                        if (store.getLocationName().contains(storeQuery)) {
                            matchedStores.add(store);

                            Log.i(TAG, "added store +" + store.getLocationName());
                            customAdapter.notifyDataSetChanged();
                        } else if (store.getLocationAddress().contains(storeQuery)) {
                            matchedStores.add(store);
                            Log.i(TAG, "added store +" + store.getLocationName());
                            customAdapter.notifyDataSetChanged();
                        } else if (store.getLocationType().contains(storeQuery)) {
                            matchedStores.add(store);
                            Log.i(TAG, "added store +" + store.getLocationName());
                            customAdapter.notifyDataSetChanged();
                        }
                    }

                }
                return true;
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference polls = database.getReference();
        polls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (!(child.getValue() instanceof Map)) {
                        continue;
                    }
                    String hashKey = child.getKey();
                    Map<String, Object> store = (Map<String, Object>) child.getValue();

                    try {

                        String address = store.get("Address").toString();
                        String name = store.get("Name").toString();
                        String type = store.get("Store Type").toString();
                        float rating = 0;
                        if (store.get("Store Rating") != null) {
                            rating = (float) store.get("Store Rating");
                        }
                        String storeName = (String) store.get("Name");
                        if (storeName == null) {
                            storeName = "N/A";
                        }
                        List<SearchFragment.Review> list_of_reviews = new ArrayList<SearchFragment.Review>();
                        if (store.containsKey("Reviews")) {
                            List<Map<String, Object>> reviews = (List<Map<String, Object>>) store.get("Reviews");

                            for (Map<String, Object> review : reviews) {
                                int price = Integer.parseInt(review.get("Price").toString());
                                int freshness = Integer.parseInt(review.get("Freshness").toString());
                                int overall = Integer.parseInt(review.get("Overall").toString());
                                Log.i(TAG, "price freshness overall" + price + " " + freshness + " " + overall);
                                String pattern = "EEE MMM DD HH:MM:SS ZZZ yyyy";
                                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
                                Date date = dateFormat.parse((String) review.get("Date"));
                                String review_body = review.get("Body").toString();
                                String cut_off_review = "";
                                if (review_body.length() > 15) {
                                    cut_off_review = review_body.substring(0, 15) + "...";
                                } else {
                                    cut_off_review = review_body;
                                }
                                String review_to_display = store + ":" + " Rating: " + overall + " stars" + cut_off_review;
                                SearchFragment.Review reviewObj = new SearchFragment.Review(overall, review_body, date, price, storeName, review_to_display);
                                list_of_reviews.add(reviewObj);
                                customAdapter.notifyDataSetChanged();
                            }
                        }
                        //create a store
                        Store storeObj = new Store(name, address, type, rating, hashKey, list_of_reviews);
                        list_of_stores.add(storeObj);
                    } catch (Exception e) {
                        Log.i(TAG, "ASDF " + e.toString());
                    }
                }
                Log.i(TAG, "finished adding stores :" + list_of_stores.size());

            }
        });
        ListView listView = view.findViewById(R.id.list_of_stores);

        listView.setAdapter(customAdapter);
    }

    class Review implements Comparable<SearchFragment.Review> {
        private int overall;
        private String body;
        private Date date;
        private int price;
        private String storeName;
        private String smallBody;
        private String displayBody;

        public Review(int overall, String body, Date date, int price, String storeName, String displayBody) {
            this.body = body;
            this.overall = overall;
            this.date = date;
            this.price = price;
            this.storeName = storeName;
            if (body.length() > 15) {
                smallBody = body.substring(0, 15) + "...";
            } else {
                smallBody = body;
            }
            this.displayBody = displayBody;
        }

        public String getDisplayBody() {
            return this.displayBody;
        }

        public int getOverall() {
            return overall;
        }

        public String getName() {
            return storeName;
        }

        public String getReviewBody() {
            return body;
        }


        @Override
        public int compareTo(Review review) {
            return this.date.compareTo(review.date);
        }
    }

    class Store {
        private String locationName;
        private String locationAddress;
        private String locationType;
        private float rating;
        private String hashKey;
        private List<Review> reviews;

        public Store(String locationName, String locationAddress, String locationType, float rating
                , String hashKey, List<Review> reviews) {
            this.locationAddress = locationAddress;
            this.locationName = locationName;
            this.locationType = locationType;
            this.rating = rating;
            this.hashKey = hashKey;
            this.reviews = reviews;
        }

        public List<Review> getReviews() {
            return reviews;
        }

        public String getHashKey() {
            return hashKey;
        }

        public String getLocationAddress() {
            return this.locationAddress;
        }

        public String getLocationName() {
            return this.locationName;
        }

        public String getLocationType() {
            return this.locationType;
        }

        public float getRating() {
            return rating;
        }
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return matchedStores.size();
        }

        @Override
        public Object getItem(int i) {
            return matchedStores.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.single_store, null);

            SearchFragment.Store thisStore = matchedStores.get(i);
            Log.i(TAG,"printing out store " + thisStore.getLocationName());
            TextView reviewBoldTitleTextView = view.findViewById(R.id.singleStoreBoldTitle);
            reviewBoldTitleTextView.setText(thisStore.getLocationName());
            //TextView reviewTextView = view.findViewById(R.id.singleReview);
            AppCompatRatingBar ratingBar = view.findViewById(R.id.storeRatingBar);
            ratingBar.setRating(thisStore.getRating());
            //ratingBar.setEnabled(false);

            return view;
        }
    }
}
