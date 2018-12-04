package com.example.daniel.tastet;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchFragment extends Fragment {
    public static final String TAG = "TasteT";
    EditText searchBox;
    String storeQuery;
    ArrayList<Store> list_of_stores = new ArrayList<Store>();
    ArrayList<Store> matchedStores = new ArrayList<Store>();
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
                        actionId == EditorInfo.IME_ACTION_GO || (event != null &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    storeQuery = searchBox.getText().toString();
                    Log.i(TAG, "searched for " + storeQuery);
                    searchBox.setText("", TextView.BufferType.EDITABLE);

                    //query based on result
                    matchedStores.clear();
                    customAdapter.notifyDataSetChanged();
                    for (Store store : list_of_stores) {
                        if (store.getLocationName().contains(storeQuery) ||
                                store.getLocationAddress().contains(storeQuery) ||
                                store.getLocationType().contains(storeQuery)) {
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
            public void onCancelled(DatabaseError databaseError) {}

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (!(child.getValue() instanceof Map)) {
                        continue;
                    }
                    String hashKey = child.getKey();
                    Map<String, Object> store = (Map<String, Object>) child.getValue();

                    String storeName = store.get("Name").toString();
                    String storeAddress = store.get("Address").toString();
                    String storeType = store.get("Store Type").toString();

                    ArrayList<Review> list_of_reviews = new ArrayList<Review>();
                    if (store.containsKey("Reviews")) {
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
                    //create a store
                    Store storeObj = new Store(storeName, storeAddress, storeType, list_of_reviews, hashKey);

                    list_of_stores.add(storeObj);
                    customAdapter.notifyDataSetChanged();
                }
                Log.i(TAG, "finished adding stores :" + list_of_stores.size());

            }
        });

        ListView listView = view.findViewById(R.id.list_of_stores);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Store data =  (Store) adapterView.getItemAtPosition(i);

                Intent storePage = new Intent(SearchFragment.this.getContext(), StorePageActivity.class);
                data.packageIntent(storePage);
                SearchFragment.this.getActivity().startActivity(storePage);
            }
        });
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

            Store thisStore = matchedStores.get(i);
            Log.i(TAG,"printing out store " + thisStore.getLocationName());
            TextView reviewBoldTitleTextView = view.findViewById(R.id.singleStoreBoldTitle);
            reviewBoldTitleTextView.setText(thisStore.getLocationName());
            //TextView reviewTextView = view.findViewById(R.id.singleReview);
            AppCompatRatingBar ratingBar = view.findViewById(R.id.storeRatingBar);
            ratingBar.setRating(thisStore.getOverallRating());
            //ratingBar.setEnabled(false);

            return view;
        }
    }
}
