package com.learn.shruti.ratemyday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ShowDataActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private ArrayList<Review> reviewList;
    private List<Review> reviewsearchList;
    private ReviewAdapter mReviewAdapter;
    private FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    private ReviewAdapter rvadapter;
    MenuItem searchItem;
    SearchView searchView;
    FloatingActionButton plotfab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        reviewList = new ArrayList<Review>();

        reviewsearchList = new ArrayList<>();




        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        rvadapter = new ReviewAdapter(reviewList);
        auth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("reviews");
        try {
            getDataFromFirebase();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Data fetch failed! Please try again ",Toast.LENGTH_SHORT).show();
        }


        rvadapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(rvadapter);
        //rvadapter.notifyDataSetChanged();



        alarmSetter();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        getMenuInflater().inflate(R.menu.menuaftersearch, menu);


        searchItem = menu.findItem(R.id.search);

        searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search) + "</font>"));



        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(!isFocused)
                {
                    //Toast.makeText(ShowDataActivity.this,"bar not focused now",Toast.LENGTH_SHORT).show();
                    searchView.setQuery("", false);
                    reviewsearchList.clear();
                    rvadapter = new ReviewAdapter(reviewList);
                    rvadapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(rvadapter);
                    rvadapter.notifyDataSetChanged();
                }
            }
        });
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
                        if(r.dateOfReview.equalsIgnoreCase( query))
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
                    else
                    {
                        rvadapter = new ReviewAdapter(reviewsearchList);
                        rvadapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(rvadapter);
                        rvadapter.notifyDataSetChanged();
                    }
                }

                //Toast.makeText(ShowDataActivity.this,"submitted",Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(ShowDataActivity.this,"textchanged",Toast.LENGTH_SHORT).show();
                reviewsearchList.clear();
                rvadapter.notifyDataSetChanged();
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




    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.rate:
                //Toast.makeText(ShowDataActivity.this, "rating selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ShowDataActivity.this,RateDayActivity.class));
                break;
            case R.id.anonymmenu:
                startActivity(new Intent(ShowDataActivity.this,SafeCompActivity.class));
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ShowDataActivity.this,LoginSignupActivity.class));
                //Toast.makeText(ShowDataActivity.this, "logout selected", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }



        // method to get reviews from the review db
        private void getDataFromFirebase()
        {
            user = auth.getCurrentUser();
            final String userEmail = user.getEmail();


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        reviewList.clear();
                        rvadapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        Review r = postSnapshot.getValue(Review.class);

                        // checking so that detail of right user is fetched and data is not repeated in list
                        if(r.employeeEmail.equals(userEmail))
                            if(!reviewList.contains(r))
                                reviewList.add(r);

                        }

                    Collections.sort(reviewList, new Comparator<Review>() {
                        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                        @Override
                        public int compare(Review o1, Review o2) {
                            try {
                                return f.parse(o2.dateOfReview).compareTo(f.parse(o1.dateOfReview));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });
                    rvadapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("SOme Tag: ", "Failed to read value.", error.toException());
                }
            });

        }


        // alarm setting to set notification to raise alert at 8 pm to send review
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
            alarmStartTime.set(Calendar.HOUR_OF_DAY, 16);
            alarmStartTime.set(Calendar.MINUTE, 0);
            alarmStartTime.set(Calendar.SECOND, 0);

            if (now.after(alarmStartTime)) {

                alarmStartTime.add(Calendar.DATE, 1);
            }

        /* Repeating every day at 8 pm */
            manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }


    @Override
    public void onBackPressed() {

        searchItem.expandActionView();

        searchView.setQuery("", false);
        searchView.clearFocus();
        reviewsearchList.clear();
        rvadapter = new ReviewAdapter(reviewList);
        mRecyclerView.setAdapter(rvadapter);
        rvadapter.notifyDataSetChanged();
        super.onBackPressed();
    }
}
