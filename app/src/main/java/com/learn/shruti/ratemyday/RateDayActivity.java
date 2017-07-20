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

import java.util.Date;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RateDayActivity extends AppCompatActivity {

    RadioButton absentradiobut;
    RatingBar dayratingbar;
    EditText feedbacktext;
    Button saveratingbut;
    private FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_day);

        absentradiobut = (RadioButton)findViewById(R.id.absentradiobutton);
        dayratingbar = (RatingBar)findViewById(R.id.dayratingBar);
        feedbacktext = (EditText)findViewById(R.id.feedbacktext);
        saveratingbut = (Button)findViewById(R.id.saveratingbutton);


        auth = FirebaseAuth.getInstance();


        absentradiobut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(absentradiobut.isChecked())
                {
                    dayratingbar.setRating(0);
                    feedbacktext.setText("NA");
                }
            }
        });
        saveratingbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeRatingsToFirebase();
                startActivity(new Intent(RateDayActivity.this, ShowDataActivity.class));
            }
        });

    }

    private void writeRatingsToFirebase()
    {
        user = auth.getCurrentUser();
        final String userEmail = user.getEmail();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query usernamequery = mDatabase.child("users").orderByChild("empEmail").equalTo(userEmail);
        usernamequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Employee r = postSnapshot.getValue(Employee.class);

                    //review is only added once user with auth email is found
                    if(r.empEmail == userEmail)
                    {

                        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference("reviews");
                        Review newrev = new Review(dayratingbar.getRating(),new Date().toString(),
                                feedbacktext.getText().toString(),r.empName,userEmail);

                        // pushing review to 'reviews' node using the uniqueID
                        uDatabase.child(uDatabase.push().getKey()).setValue(newrev);

                        Toast.makeText(RateDayActivity.this,"Saved",Toast.LENGTH_SHORT).show();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
