package com.learn.shruti.ratemyday;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.learn.shruti.ratemyday.Model.Review;

import java.util.List;

/**
 * Created by Shruti on 22/07/2017.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>  {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent,false));
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        Review r = reviewList.get(position);

        holder.comment.setText("Feedback: " + r.comments);
        holder.reviewDate.setText("Dated: "+r.dateOfReview);
        holder.rating.setText("Rating: "+ String.valueOf( r.rating));
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView rating;
        TextView reviewDate;
        TextView comment;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            rating = (TextView) itemView.findViewById(R.id.tvrating);
            reviewDate = (TextView) itemView.findViewById(R.id.tvdate);
            comment = (TextView) itemView.findViewById(R.id.tvmsg);
        }
    }
}
