package com.example.daniel.tastet;

import android.content.Intent;

import java.io.Serializable;
import java.util.Date;

public class Review implements Comparable<Review>, Serializable {

    private static final String REVIEW_OBJ_TITLE = "REVIEW_OBJ_TITLE";
    private static final String REVIEW_OBJ_USER = "REVIEW_OBJ_USER";
    private static final String REVIEW_OBJ_OVERALL_RATING = "REVIEW_OBJ_OVERALL_RATING";
    private static final String REVIEW_OBJ_FRESHNESS_RATING = "REVIEW_OBJ_FRESHNESS_RATING";
    private static final String REVIEW_OBJ_TASTE_RATING = "REVIEW_OBJ_TASTE_RATING";
    private static final String REVIEW_OBJ_PRICE_RATING = "REVIEW_OBJ_PRICE_RATING";
    private static final String REVIEW_OBJ_TEXT = "REVIEW_OBJ_TEXT";
    private static final String REVIEW_OBJ_STORENAME = "REVIEW_OBJ_STORENAME";
    private static final String REVIEW_OBJ_DATE = "REVIEW_OBJ_DATE";

    private String title;
    private String user;
    private float overallRating;
    private float freshnessRating;
    private float tasteRating;
    private float priceRating;
    private String text;
    private String storeName;
    private Date date;

    private String smallBody;

    public Review(String title, String user, float overallRating, float freshnessRating,
                  float tasteRating, float priceRating, String text, String storeName, Date date) {
        this.title = title;
        this.user = user;
        this.overallRating = overallRating;
        this.freshnessRating = freshnessRating;
        this.tasteRating = tasteRating;
        this.priceRating = priceRating;
        this.text = text;
        this.storeName = storeName;
        this.date = date;

        if (this.text.length() > 15) {
            smallBody = this.text.substring(0, 15) + "...";
        } else {
            smallBody = this.text;
        }
    }

    public Review(Intent i) {
        this.title = i.getStringExtra(REVIEW_OBJ_TITLE);
        this.user = i.getStringExtra(REVIEW_OBJ_USER);
        this.overallRating = i.getFloatExtra(REVIEW_OBJ_OVERALL_RATING, 3f);
        this.freshnessRating = i.getFloatExtra(REVIEW_OBJ_FRESHNESS_RATING, 3f);
        this.tasteRating = i.getFloatExtra(REVIEW_OBJ_TASTE_RATING, 3f);
        this.priceRating = i.getFloatExtra(REVIEW_OBJ_PRICE_RATING, 3f);
        this.text = i.getStringExtra(REVIEW_OBJ_TEXT);
        this.storeName = i.getStringExtra(REVIEW_OBJ_STORENAME);
        this.date = (Date) i.getSerializableExtra(REVIEW_OBJ_DATE);

        if (this.text.length() > 15) {
            smallBody = this.text.substring(0, 15) + "...";
        } else {
            smallBody = this.text;
        }
    }

    public void packageIntent(Intent i) {
        i.putExtra(REVIEW_OBJ_TITLE, title);
        i.putExtra(REVIEW_OBJ_USER, user);
        i.putExtra(REVIEW_OBJ_OVERALL_RATING, overallRating);
        i.putExtra(REVIEW_OBJ_FRESHNESS_RATING, freshnessRating);
        i.putExtra(REVIEW_OBJ_TASTE_RATING, tasteRating);
        i.putExtra(REVIEW_OBJ_PRICE_RATING, priceRating);
        i.putExtra(REVIEW_OBJ_TEXT, text);
        i.putExtra(REVIEW_OBJ_STORENAME, storeName);
        i.putExtra(REVIEW_OBJ_DATE, date);
    }

    public String getTitle() {return title;}

    public String getUser() {return user;}

    public float getOverallRating() {return overallRating;}

    public float getFreshnessRating() {return freshnessRating;}

    public float getTasteRating() {return tasteRating;}

    public float getPriceRating() {return priceRating;}

    public String getText() {return text;}

    public String getStoreName() {return storeName;}

    public Date getDate() {return date;}

    public String getSmallBody() {return smallBody;}


    @Override
    public int compareTo(Review review) {
        return this.date.compareTo(review.date);
    }
}
