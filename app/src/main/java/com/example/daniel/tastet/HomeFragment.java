package com.example.daniel.tastet;

import java.util.Collections;

import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;

import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
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
    public void onViewCreated(View view,Bundle savedInstanceState){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference polls = database.getReference();
        polls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if(!(child.getValue() instanceof Map)){
                        continue;
                    }
                    Map<String, Object> hash = (Map<String, Object>) child.getValue();

                    try {
                        String storeName =  (String)hash.get("Name");
                        if(storeName == null){
                            storeName = "N/A";
                        }
                        if(hash.containsKey("Reviews")){
                            List<Map<String,Object>> reviews = (List<Map<String,Object>>)hash.get("Reviews");

                            for(Map<String,Object> review : reviews){
                                int price = Integer.parseInt(review.get("Price").toString());
                                int freshness = Integer.parseInt(review.get("Freshness").toString());
                                int overall = Integer.parseInt(review.get("Overall").toString());
                                Log.i(TAG,"price freshness overall" + price + " " + freshness + " " + overall);
                                String pattern = "EEE MMM DD HH:MM:SS ZZZ yyyy";
                                SimpleDateFormat dateFormat= new SimpleDateFormat(pattern,Locale.US);
                                Date date = dateFormat.parse((String) review.get("Date"));
                                String review_body = review.get("Body").toString();
                                String cut_off_review = "";
                                if(review_body.length() > 15){
                                    cut_off_review = review_body.substring(0,15) + "...";
                                }else{
                                    cut_off_review = review_body;
                                }
                                String review_to_display = storeName + ":" + " Rating: " + overall + " stars" +  cut_off_review;
                                Review reviewObj = new Review(overall,review_body,date,price,storeName,review_to_display);
                                list_of_reviews.add(reviewObj);
                                customAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        ListView listView = view.findViewById(R.id.list_of_items);

        listView.setAdapter(customAdapter);

    }
    class Review implements Comparable<Review>{
        private int overall;
        private String body;
        private Date date;
        private int price;
        private String storeName;
        private String smallBody;
        private String displayBody;
        public Review(int overall,String body,Date date,int price,String storeName,String displayBody){
            this.body = body;
            this.overall = overall;
            this.date = date;
            this.price = price;
            this.storeName = storeName;
            if(body.length() > 15){
                smallBody = body.substring(0,15) + "...";
            }else{
                smallBody = body;
            }
            this.displayBody = displayBody;
        }
        public String getDisplayBody(){
            return this.displayBody;
        }
        public int getOverall(){
            return overall;
        }
        @Override
        public int compareTo(Review other){
            return this.date.compareTo(other.date);
        }
    }
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list_of_reviews.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Collections.sort(list_of_reviews);
            view = getLayoutInflater().inflate(R.layout.single_review,null);
            TextView reviewTextView = view.findViewById(R.id.singleReview);
            AppCompatRatingBar ratingBar = view.findViewById(R.id.ratingBar);
            ratingBar.setRating(list_of_reviews.get(i).getOverall());
            //ratingBar.setEnabled(false);
            reviewTextView.setText(list_of_reviews.get(i).getDisplayBody());
            return view;
        }
    }
}
