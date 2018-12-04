package com.example.daniel.tastet;

import java.text.ParseException;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;

import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;


        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;


        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;
        import java.util.Map;

import android.widget.AdapterView;
import android.widget.BaseAdapter;

        import android.widget.ListView;

        import android.widget.TextView;

public class HomeFragment extends Fragment {

    public static final String TAG = "TasteT";
    ArrayList<Review> list_of_reviews = new ArrayList<Review>();
    CustomAdapter customAdapter = new CustomAdapter();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment,null);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference polls = database.getReference();
        polls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {}
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear any existing data
                list_of_reviews.clear();
                customAdapter.notifyDataSetChanged();

                for (DataSnapshot child : snapshot.getChildren()) {
                    if(!(child.getValue() instanceof Map)){
                        continue;
                    }
                    Map<String, Object> store = (Map<String, Object>) child.getValue();

                        String storeName =  (String)store.get("Name");

                        if(store.containsKey("Reviews")){
                            List<Map<String,Object>> reviews = (List<Map<String,Object>>)store.get("Reviews");

                            for(Map<String,Object> review : reviews){


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
                }
                customAdapter.notifyDataSetChanged();
            }
        });
        ListView listView = view.findViewById(R.id.home_list_of_items);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Review data = (Review) adapterView.getItemAtPosition(i);

                Intent storePage = new Intent(HomeFragment.this.getContext(), ReviewPageActivity.class);
                data.packageIntent(storePage);
                HomeFragment.this.getActivity().startActivity(storePage);
            }
        });

    }

    class CustomAdapter extends BaseAdapter{

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
