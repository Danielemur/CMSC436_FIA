package com.example.daniel.tastet;

import java.util.Date;

public class Review implements Comparable<Review> {

    private String title;
    private String user;
    private float overall;
    private float freshness;
    private float taste;
    private float price;
    private String body;
    private Date date;
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
        return (int) overall;
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
