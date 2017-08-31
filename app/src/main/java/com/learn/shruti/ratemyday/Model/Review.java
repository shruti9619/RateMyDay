package com.learn.shruti.ratemyday.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Shruti on 18/07/2017.
 */
public class Review implements Serializable{
    public double rating;
    public String dateOfReview;
    public String comments;
    public String employeeName;
    public String employeeEmail;

    public Review(double rating, String dateOfReview, String comments) {
        this.rating = rating;
        this.dateOfReview = dateOfReview;
        this.comments = comments;
    }

    public Review(double rating, String dateOfReview, String comments, String employeeName, String employeeEmail) {
        this.rating = rating;
        this.dateOfReview = dateOfReview;
        this.comments = comments;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
    }

    public Review() {
    }
}
