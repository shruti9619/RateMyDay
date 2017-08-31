package com.learn.shruti.ratemyday.Model;

/**
 * Created by Shruti on 31/08/2017.
 */
public class AnonymousComplaint {

    String Issue;
    String details;
    String dateOfIssue;


    public AnonymousComplaint() {
    }

    public AnonymousComplaint(String issue, String details, String dateOfIssue) {
        Issue = issue;
        this.details = details;
        this.dateOfIssue = dateOfIssue;
    }
}
