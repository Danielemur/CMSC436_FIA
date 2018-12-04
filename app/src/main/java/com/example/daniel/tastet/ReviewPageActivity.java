package com.example.daniel.tastet;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class ReviewPageActivity extends Activity {
    private Review data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.review_page_activity);

        data = new Review(this.getIntent());


        TextView reviewStoreNameView = this.findViewById(R.id.review_page_store_name);
        TextView reviewTitleUserView = this.findViewById(R.id.review_page_title_user);
        TextView reviewDateView = this.findViewById(R.id.review_page_date);
        RatingBar reviewOverallRatingView = this.findViewById(R.id.review_page_overall_rating);
        RatingBar reviewFreshnessRatingView = this.findViewById(R.id.review_page_freshness_rating);
        RatingBar reviewTasteRatingView = this.findViewById(R.id.review_page_taste_rating);
        RatingBar reviewPriceRatingView = this.findViewById(R.id.review_page_price_rating);
        TextView reviewCommentView = this.findViewById(R.id.review_page_comment);


        reviewStoreNameView.setText(data.getStoreName());
        reviewTitleUserView.setText(data.getTitle() + " by " + data.getUser());

        String pattern_time = "HH:mma";
        String pattern_date = "EEEEEEEEE, MMM. d, yyyy";
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern_time);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern_date);

        String dateStr = timeFormat.format(data.getDate()) + " on " + dateFormat.format(data.getDate());
        reviewDateView.setText(dateStr);
        reviewOverallRatingView.setRating(data.getOverallRating());
        reviewFreshnessRatingView.setRating(data.getFreshnessRating());
        reviewTasteRatingView.setRating(data.getTasteRating());
        reviewPriceRatingView.setRating(data.getPriceRating());
        String body = data.getText();
        if (body.length() == 0) {
            body = "None";
        }
        reviewCommentView.setText(body);
    }
}
