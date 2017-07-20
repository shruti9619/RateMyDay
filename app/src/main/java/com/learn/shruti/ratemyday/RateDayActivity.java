package com.learn.shruti.ratemyday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learn.shruti.ratemyday.Model.Review;

import java.util.Date;

public class RateDayActivity extends AppCompatActivity {

    RadioButton absentradiobut;
    RatingBar dayratingbar;
    EditText feedbacktext;
    Button saveratingbut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_day);

        absentradiobut = (RadioButton)findViewById(R.id.absentradiobutton);
        dayratingbar = (RatingBar)findViewById(R.id.dayratingBar);
        feedbacktext = (EditText)findViewById(R.id.feedbacktext);
        saveratingbut = (Button)findViewById(R.id.saveratingbutton);



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
            }
        });

    }

    private void writeRatingsToFirebase()
    {

       DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("reviews");
       Review newrev = new Review(Double.valueOf(dayratingbar.getRating()),new Date().toString(),
               feedbacktext.getText().toString(),"Jingle","22");

       //Toast.makeText(this,mDatabase.toString(),Toast.LENGTH_SHORT).show();

        // pushing review to 'reviews' node using the uniqueID
        mDatabase.child(mDatabase.push().getKey()).setValue(newrev);

    }
}
