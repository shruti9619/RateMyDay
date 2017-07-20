package com.learn.shruti.ratemyday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RateDayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_day);

        //            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

            /*Review newrev = new Review(9.0,"3/1/2017","phone comments","Jingle","22");

            //Toast.makeText(this,mDatabase.toString(),Toast.LENGTH_SHORT).show();
// pushing user to 'users' node using the userId
            mDatabase.child(mDatabase.push().getKey()).setValue(newrev);*/


    }
}
