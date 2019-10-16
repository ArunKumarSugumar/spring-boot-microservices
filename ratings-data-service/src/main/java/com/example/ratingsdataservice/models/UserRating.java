package com.example.ratingsdataservice.models;

import java.util.List;

/**
 * @author ArunKumar.Sugumar
 */
public class UserRating {

    private List<Rating> userRatings;

    public List<Rating> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(List<Rating> userRatings) {
        this.userRatings = userRatings;
    }
}
