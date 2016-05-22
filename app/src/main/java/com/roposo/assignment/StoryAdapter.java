package com.roposo.assignment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roposo.assignment.data.DataManager;
import com.roposo.assignment.data.Story;
import com.roposo.assignment.data.User;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {


    private List<Story> stories;
    private Context context;

    public StoryAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new StoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.story_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position) {
        holder.bindData(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        ImageView userProfileImageView;
        TextView followToggleTextView;
        TextView userNameTextView;
        TextView titleTextView;
        ImageView storyContentImageView;
        TextView commentCountTextView;
        TextView likeCountTextView;
        Button likeButton;
        Button commentsButton;

        public StoryViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            userProfileImageView = (ImageView) rootView.findViewById(R.id.user_image_view);
            followToggleTextView = (TextView) rootView.findViewById(R.id.follow_unfollow_text_view);
            userNameTextView = (TextView) rootView.findViewById(R.id.user_name_text_view);
            titleTextView = (TextView) rootView.findViewById(R.id.story_title);
            storyContentImageView = (ImageView) rootView.findViewById(R.id.story_content_image);
            commentCountTextView = (TextView) rootView.findViewById(R.id.comment_count_textview);
            likeCountTextView = (TextView) rootView.findViewById(R.id.like_count_textview);
            likeButton = (Button) rootView.findViewById(R.id.like_button);
            commentsButton = (Button) rootView.findViewById(R.id.comment_button);
        }

        public void bindData(final Story story) {
            final Context context = userProfileImageView.getContext();
            User user = DataManager.getInstance().getUser(story.getDb());
            /*Picasso.with(userProfileImageView.getContext())
                    .load(user.getImage())
                    .resize(40, 40)
                    .into(userProfileImageView);
            Picasso.with(storyContentImageView.getContext())
                    .load(story.getSi())
                    .into(storyContentImageView);*/
            setFollow(user.is_following());
            userNameTextView.setText(user.getUsername());
            titleTextView.setText(story.getTitle());
            String comment = context.getResources().getQuantityString(R.plurals.comment_count,
                    story.getCommentCount(), story.getCommentCount());
            commentCountTextView.setText(comment);
            String like = context.getResources().getQuantityString(R.plurals.like_count,
                    story.getLikesCount(), story.getLikesCount());
            likeCountTextView.setText(like);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (story.isLikeFlag()) {
                        story.setLikeFlag(false);
                    } else {
                        story.setLikeFlag(true);
                    }
                    updateLikeButtonState(story, context);
                }
            });
            commentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), R.string.comments_error_message, Toast.LENGTH_SHORT).show();
                }
            });
            updateLikeButtonState(story, context);
        }

        private void updateLikeButtonState(Story story, Context context) {

            if (story.isLikeFlag()) {
                Drawable drawable = getDrawable(context, R.drawable.ic_favorite_24dp);
                likeButton.setCompoundDrawables(drawable, null, null, null);
            } else {
                Drawable drawable = getDrawable(context, R.drawable.ic_favorite_border_black_24dp);
                likeButton.setCompoundDrawables(drawable, null, null, null);
            }
        }

        @NonNull
        private Drawable getDrawable(Context context, int ic_favorite_24dp) {
            Drawable drawable = ActivityCompat.getDrawable(context, ic_favorite_24dp);
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            drawable.setBounds(0, 0, w, h);
            return drawable;
        }

        private void setFollow(boolean following) {
            if (following) {
                followToggleTextView.setText("Following");
            } else {
                followToggleTextView.setText("");
            }
        }
    }
}
