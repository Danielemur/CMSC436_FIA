package com.example.daniel.tastet;


        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;

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
    ArrayList<String> list_of_reviews = new ArrayList<String>();
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

                                list_of_reviews.add(review_to_display);
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
            view = getLayoutInflater().inflate(R.layout.single_review,null);
            TextView reviewTextView = view.findViewById(R.id.singleReview);
            reviewTextView.setText(list_of_reviews.get(i));
            return view;
        }
    }
}
