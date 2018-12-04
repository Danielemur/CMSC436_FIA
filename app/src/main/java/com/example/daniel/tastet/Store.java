package com.example.daniel.tastet;

import android.content.Intent;

import java.util.List;

public class Store {

public static final String STORE_OBJ_NAME = "STORE_OBJ_NAME";
public static final String STORE_OBJ_ADDRESS = "STORE_OBJ_ADDRESS";
public static final String STORE_OBJ_TYPE = "STORE_OBJ_TYPE";
public static final String STORE_OBJ_RATING = "STORE_OBJ_RATING";
public static final String STORE_OBJ_HASHKEY = "STORE_OBJ_HASHKEY";
//TODO
//public static final String STORE_OBJ_REVIEWS = "STORE_OBJ_REVIEWS";

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

    public Store(Intent i) {
        this.locationName = i.getStringExtra(STORE_OBJ_NAME);
        this.locationAddress = i.getStringExtra(STORE_OBJ_ADDRESS);
        this.locationType = i.getStringExtra(STORE_OBJ_TYPE);
        this.rating = i.getFloatExtra(STORE_OBJ_RATING, 3f);
        this.hashKey = i.getStringExtra(STORE_OBJ_HASHKEY);
        //TODO
        //this.reviews = i.getExtras();
    }

    public void packageIntent(Intent i) {
        i.putExtra(STORE_OBJ_NAME, locationName);
        i.putExtra(STORE_OBJ_ADDRESS, locationAddress);
        i.putExtra(STORE_OBJ_TYPE, locationType);
        i.putExtra(STORE_OBJ_RATING, rating);
        i.putExtra(STORE_OBJ_HASHKEY, hashKey);
        //TODO
        //i.putExtra(STORE_OBJ_REVIEWS, reviews);
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
