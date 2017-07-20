package com.learn.shruti.ratemyday;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.learn.shruti.ratemyday.Model.Review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowDataActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter mReviewAdapter;

    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        reviewList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("reviews");
        try {
            getDataFromFirebase();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"there was an except "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewAdapter = new ReviewAdapter();
        mRecyclerView.setAdapter(mReviewAdapter);


        mReviewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                reviewList.add(null);
                mReviewAdapter.notifyItemInserted(reviewList.size() - 1);


                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {

                        //Remove loading item
                        reviewList.remove(reviewList.size() - 1);
                mReviewAdapter.notifyItemRemoved(reviewList.size());
                //Load data
                int index = reviewList.size();
                //here add code to add more data to list
               /* int end = index + 20;
                for (int i = index; i < end; i++) {
                    Review user = new Review(i,new Date().toString(),"alibaba" + i + "@gmail.com");
                    reviewList.add(user);
                }*/
                mReviewAdapter.notifyDataSetChanged();
                mReviewAdapter.setLoaded();
            }
        }, 5000);
    }
});




    }


        static class UserViewHolder extends RecyclerView.ViewHolder {
            public TextView tvrating;
            public TextView tvdate;
            public TextView tvmsg;

            public UserViewHolder(View itemView) {
                super(itemView);
                tvrating = (TextView) itemView.findViewById(R.id.tvrating);
                tvdate = (TextView) itemView.findViewById(R.id.tvdate);
                tvmsg = (TextView) itemView.findViewById(R.id.tvmsg);
            }
        }


        static class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            }
        }


        class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            private final int VIEW_TYPE_ITEM = 0;
            private final int VIEW_TYPE_LOADING = 1;
            private OnLoadMoreListener mOnLoadMoreListener;
            private boolean isLoading;
            private int visibleThreshold = 5;
            private int lastVisibleItem, totalItemCount;

            public ReviewAdapter() {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();


                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                            }
                            isLoading = true;
                        }
                    }
                });
            }

            public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
                this.mOnLoadMoreListener = mOnLoadMoreListener;
            }

            @Override
            public int getItemViewType(int position) {
                return reviewList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == VIEW_TYPE_ITEM) {
                    View view = LayoutInflater.from(ShowDataActivity.this).inflate(R.layout.layout_user_item, parent, false);
                    return new UserViewHolder(view);
                } else if (viewType == VIEW_TYPE_LOADING) {
                    View view = LayoutInflater.from(ShowDataActivity.this).inflate(R.layout.layout_loading_item, parent, false);
                    return new LoadingViewHolder(view);
                }
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof UserViewHolder) {
                    Review r = reviewList.get(position);
                    UserViewHolder userViewHolder = (UserViewHolder) holder;


                    userViewHolder.tvrating.setText(String.valueOf(r.rating));
                    userViewHolder.tvdate.setText(r.dateOfReview.toString());
                    userViewHolder.tvmsg.setText(r.comments.toString());

                } else if (holder instanceof LoadingViewHolder) {
                    LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                    loadingViewHolder.progressBar.setIndeterminate(true);
                }
            }

            @Override
            public int getItemCount() {
                return reviewList == null ? 0 : reviewList.size();
            }

            public void setLoaded() {
                isLoading = false;
            }
        }


        public void getDataFromFirebase()
        {
//            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

            /*Review newrev = new Review(9.0,"3/1/2017","phone comments","Jingle","22");

            //Toast.makeText(this,mDatabase.toString(),Toast.LENGTH_SHORT).show();
// pushing user to 'users' node using the userId
            mDatabase.child(mDatabase.push().getKey()).setValue(newrev);*/

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        Review r = postSnapshot.getValue(Review.class);
                        reviewList.add(r);
                        //Toast.makeText(ShowDataActivity.this,"com: " + r.comments + ", rate " + r.rating,Toast.LENGTH_SHORT).show();
                         }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("SOme Tag: ", "Failed to read value.", error.toException());
                }
            });

        }


}
