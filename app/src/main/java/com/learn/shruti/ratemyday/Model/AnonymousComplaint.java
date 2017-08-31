package com.learn.shruti.ratemyday.Model;

import java.io.Serializable;

/**
 * Created by Shruti on 31/08/2017.
 */
public class AnonymousComplaint implements Serializable{

    public String Issue;
    public String details;
    public String dateOfIssue;


    public AnonymousComplaint() {
    }

    public AnonymousComplaint(String issue, String details, String dateOfIssue) {
        Issue = issue;
        this.details = details;
        this.dateOfIssue = dateOfIssue;
    }
}
