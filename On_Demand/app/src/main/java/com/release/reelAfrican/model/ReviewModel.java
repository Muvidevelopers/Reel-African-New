package com.release.reelAfrican.model;

import java.io.Serializable;

/**
 * Created by User on 29-06-2017.
 */
public class ReviewModel implements Serializable {
    private String muviId;
    private String ratingValue = "";
    private String reviewValue = "";

    public String getMuviId() {
        return muviId;
    }

    public void setMuviId(String muviId) {
        this.muviId = muviId;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getReviewValue() {
        return reviewValue;
    }

    public void setReviewValue(String reviewValue) {
        this.reviewValue = reviewValue;
    }
}
