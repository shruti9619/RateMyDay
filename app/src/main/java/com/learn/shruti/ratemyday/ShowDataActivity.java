package com.learn.shruti.ratemyday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.learn.shruti.ratemyday.Model.Employee;
import com.learn.shruti.ratemyday.Model.Review;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ShowDataActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private List<Review> reviewList;
    private List<Review> reviewsearchList;
    private ReviewAdapter mReviewAdapter;
    private FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    private ReviewAdapter rvadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        reviewList = new ArrayList<>();
        reviewsearchList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        auth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("reviews");
        try {
            getDataFromFirebase();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Data fetch failed! Please try again ",Toast.LENGTH_SHORT).show();
        }

        rvadapter = new ReviewAdapter(reviewList);

        alarmSetter();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        //searchView.setSearchableInfo(
          //      searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.length()!=10)
                    Toast.makeText(ShowDataActivity.this,"Format dd/mm/yyyy",Toast.LENGTH_SHORT).show();
                else
                {
                    Boolean flag = false;
                    for (Review r : reviewList )
                    {
                        if(r.dateOfReview == query)
                        {
                            //DONT CLEAR THE Main list . rather create a new list which has your single element and then
                            // pass it to the recycleview
                            reviewsearchList.add(r);
                            flag = true;
                        }
                    }
                    if (!flag)
                    {
                        searchView.setQuery("No Matches!", false);
                    }
                }

                Toast.makeText(ShowDataActivity.this,"submitted",Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(ShowDataActivity.this,"textchanged",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return super.onCreateOptionsMenu(menu);
    }



        private void getDataFromFirebase()
        {
            user = auth.getCurrentUser();
            final String userEmail = user.getEmail();


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        Review r = postSnapshot.getValue(Review.class);

                        if(r.employeeEmail == userEmail)
                            reviewList.add(r);
                        Toast.makeText(ShowDataActivity.this,"com: " + r.comments + ", rate " + r.rating,Toast.LENGTH_SHORT).show();
                         }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("SOme Tag: ", "Failed to read value.", error.toException());
                }
            });

        }



        private void alarmSetter()
        {
            PendingIntent pendingIntent;
             /* Retrieve a PendingIntent that will perform a broadcast */
            Intent alarmIntent = new Intent(ShowDataActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(ShowDataActivity.this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        /* Set the alarm to start at 8.00 PM */

            Calendar alarmStartTime = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            alarmStartTime.set(Calendar.HOUR_OF_DAY, 1);
            alarmStartTime.set(Calendar.MINUTE, 45);
            alarmStartTime.set(Calendar.SECOND, 0);
            if (now.after(alarmStartTime)) {

                alarmStartTime.add(Calendar.DATE, 1);
            }
        /* Repeating every day at 8 pm */
            manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }

}
