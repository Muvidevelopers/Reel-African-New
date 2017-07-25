package com.release.reelAfrican.model;

/**
 * Created by User on 28-06-2017.
 */
public class ReviewsItem {

    String reviews;
    String userName;
    String rating;

    public ReviewsItem(String reviews, String userName, String rating) {
        this.reviews = reviews;
        this.userName = userName;
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}

