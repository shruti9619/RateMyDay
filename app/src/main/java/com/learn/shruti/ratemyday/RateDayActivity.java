package com.learn.shruti.ratemyday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.learn.shruti.ratemyday.Model.Employee;
import com.learn.shruti.ratemyday.Model.Review;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RateDayActivity extends AppCompatActivity {

    CheckBox absentcheckbox;
    RatingBar dayratingbar;
    EditText feedbacktext;
    Button saveratingbut;
    private FirebaseAuth auth;
    private List<Review> reviewList;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_day);

        reviewList = new ArrayList<>();

        absentcheckbox = (CheckBox)findViewById(R.id.absentcheckbox);
        dayratingbar = (RatingBar)findViewById(R.id.dayratingBar);
        feedbacktext = (EditText)findViewById(R.id.feedbacktext);
        saveratingbut = (Button)findViewById(R.id.saveratingbutton);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        initCheck();


        absentcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(absentcheckbox.isChecked())
                {
                    dayratingbar.setRating(0);
                    dayratingbar.setEnabled(false);
                    feedbacktext.setText("NA");
                    feedbacktext.setEnabled(false);
                }
                else
                {
                    dayratingbar.setRating(0);
                    dayratingbar.setEnabled(true);
                    feedbacktext.setText("");
                    feedbacktext.setEnabled(true);
                }
            }
        });
        // saving ratings of employees
        saveratingbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAlreadyRated = checkForRated();
                if(!isAlreadyRated)
                    writeRatingsToFirebase();
                else
                    Toast.makeText(RateDayActivity.this, "Today has already been rated!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(RateDayActivity.this, ShowDataActivity.class));
            }
        });

    }

    //method to return boolean to check if list size is not zero then rating for the day has already been made
    private boolean checkForRated()
    {
        if (reviewList.size() > 0)
            return true;
        else
            return false;
    }

    //method to add to list if the rating for the day has already been made
    private void initCheck()
    {
        final String userEmail = user.getEmail();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("reviews");
        mDatabase.addValueEventListener(new ValueEventListener() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Review r = postSnapshot.getValue(Review.class);
                    //checking if this user has already reviewed on this date
                    if((r.dateOfReview.equalsIgnoreCase( dateFormat.format(new Date())))&&(r.employeeEmail.equals(userEmail)))
                    {
                        reviewList.add(r);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    // method to fetch correct user and write review data in firebase
    private void writeRatingsToFirebase()
    {
        user = auth.getCurrentUser();
        try {


            final String userEmail = user.getEmail();

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            // to get reference of user where email id is equal to current user email id
            Query usernamequery = mDatabase.child("users").orderByChild("empEmail").equalTo(userEmail);

            usernamequery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        Employee r = postSnapshot.getValue(Employee.class);

                        //review is only added once user with auth email is found
                        if (r.empEmail.equalsIgnoreCase(userEmail)) {
                            String com = "";
                            DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("reviews");

                            //to check whether the user has entered any comments or not and replace empty with NA
                            if(feedbacktext.getText()!=null)
                            {
                                if (feedbacktext.getText().toString().length() <= 0)
                                    com = "NA";
                                else
                                    com = feedbacktext.getText().toString();
                            }

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Review newrev = new Review(dayratingbar.getRating(), dateFormat.format(new Date()),
                                    com, r.empName, userEmail);

                            // pushing review to 'reviews' node using the uniqueID
                            uDatabase.child(uDatabase.push().getKey()).setValue(newrev);

                            Toast.makeText(RateDayActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                            // break loop once rating has been made for the user
                            break;

                        }

                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        catch (Exception e)
        {
            //exception caught . no action required
        }

    }
}
