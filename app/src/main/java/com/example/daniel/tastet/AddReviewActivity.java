package com.example.daniel.tastet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import java.util.Date;


public class AddReviewActivity extends Activity {
    public static final String REVIEW_TITLE = "REVIEW_TITLE";
    public static final String REVIEW_USER = "REVIEW_USER";
    public static final String REVIEW_OVERALL = "REVIEW_OVERALL";
    public static final String REVIEW_FRESHNESS = "REVIEW_FRESHNESS";
    public static final String REVIEW_TASTE = "REVIEW_TASTE";
    public static final String REVIEW_PRICE = "REVIEW_PRICE";
    public static final String REVIEW_TEXT = "REVIEW_TEXT";
    public static final String REVIEW_DATE = "REVIEW_DATE";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_review_activity);

    }

    public void cancel(View v) {
        finish();
    }

    public void submit(View v) {

        // gather ToDoItem data
        EditText reviewTitleView = this.findViewById(R.id.add_review_name_entry);
        EditText reviewUserView = this.findViewById(R.id.add_review_user_entry);
        RatingBar reviewOverallView = this.findViewById(R.id.add_review_rating_overall_bar);
        RatingBar reviewFreshnessView = this.findViewById(R.id.add_review_rating_freshness_bar);
        RatingBar reviewTasteView = this.findViewById(R.id.add_review_rating_taste_bar);
        RatingBar reviewPriceView = this.findViewById(R.id.add_review_rating_price_bar);
        EditText reviewTextView = this.findViewById(R.id.add_review_text_entry);

        Intent intent = new Intent();
        intent.putExtra(REVIEW_TITLE, reviewTitleView.getText());
        intent.putExtra(REVIEW_USER, reviewUserView.getText());
        intent.putExtra(REVIEW_OVERALL, reviewOverallView.getRating());
        intent.putExtra(REVIEW_FRESHNESS, reviewFreshnessView.getRating());
        intent.putExtra(REVIEW_TASTE, reviewTasteView.getRating());
        intent.putExtra(REVIEW_PRICE, reviewPriceView.getRating());
        intent.putExtra(REVIEW_TEXT, reviewTextView.getText());
        intent.putExtra(REVIEW_DATE, new java.util.Date().toString());

        // return data Intent and finish
        setResult(RESULT_OK, intent);
        finish();

    }
}
