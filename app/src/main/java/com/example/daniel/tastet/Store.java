package com.example.daniel.tastet;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Store {

public static final String STORE_OBJ_NAME = "STORE_OBJ_NAME";
public static final String STORE_OBJ_ADDRESS = "STORE_OBJ_ADDRESS";
public static final String STORE_OBJ_TYPE = "STORE_OBJ_TYPE";
public static final String STORE_OBJ_HASHKEY = "STORE_OBJ_HASHKEY";
public static final String STORE_OBJ_REVIEWS = "STORE_OBJ_REVIEWS";

    private String locationName;
    private String locationAddress;
    private String locationType;
    private ArrayList<Review> reviews;
    private String hashKey;

    // Derived values
    private float overallRating = 0f;
    private float freshnessRating = 0f;
    private float tasteRating = 0f;
    private float priceRating = 0f;

    public Store(String locationName, String locationAddress, String locationType,
                 ArrayList<Review> reviews, String hashKey) {
        this.locationAddress = locationAddress;
        this.locationName = locationName;
        this.locationType = locationType;
        this.reviews = reviews;
        this.hashKey = hashKey;

        computeAvgRatings();
    }

    public Store(Intent i) {
        this.locationName = i.getStringExtra(STORE_OBJ_NAME);
        this.locationAddress = i.getStringExtra(STORE_OBJ_ADDRESS);
        this.locationType = i.getStringExtra(STORE_OBJ_TYPE);
        this.hashKey = i.getStringExtra(STORE_OBJ_HASHKEY);
        this.reviews = (ArrayList<Review>) i.getSerializableExtra(STORE_OBJ_REVIEWS);

        computeAvgRatings();
    }

    private void computeAvgRatings() {
        if (reviews.size() > 0) {
            overallRating = 0;
            freshnessRating = 0;
            tasteRating = 0;
            priceRating = 0;

            for (Review r : reviews) {
                overallRating += r.getOverallRating();
                freshnessRating += r.getFreshnessRating();
                tasteRating += r.getTasteRating();
                priceRating += r.getPriceRating();
            }

            overallRating /= reviews.size();
            freshnessRating /= reviews.size();
            tasteRating /= reviews.size();
            priceRating /= reviews.size();
        }
    }

    public void packageIntent(Intent i) {
        // Store all non-derived values
        i.putExtra(STORE_OBJ_NAME, locationName);
        i.putExtra(STORE_OBJ_ADDRESS, locationAddress);
        i.putExtra(STORE_OBJ_TYPE, locationType);
        i.putExtra(STORE_OBJ_HASHKEY, hashKey);
        i.putExtra(STORE_OBJ_REVIEWS, reviews);
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationType() {
        return locationType;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public String getHashKey() {
        return hashKey;
    }

    public float getOverallRating() {return overallRating;}

    public float getFreshnessRating() {return freshnessRating;}

    public float getTasteRating() {return tasteRating;}

    public float getPriceRating() {return priceRating;}

}
