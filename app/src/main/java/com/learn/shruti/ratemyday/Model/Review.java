package com.learn.shruti.ratemyday.Model;

import java.util.Date;

/**
 * Created by Shruti on 18/07/2017.
 */
public class Review {
    public double rating;
    public String dateOfReview;
    public String comments;
    public String employeeName;
    public String employeeId;

    public Review(float rating, String dateOfReview, String comments) {
        this.rating = rating;
        this.dateOfReview = dateOfReview;
        this.comments = comments;
    }

    public Review(double rating, String dateOfReview, String comments, String employeeName, String employeeId) {
        this.rating = rating;
        this.dateOfReview = dateOfReview;
        this.comments = comments;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
    }

    public Review() {
    }
}
