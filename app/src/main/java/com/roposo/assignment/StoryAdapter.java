package com.roposo.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> stories;
    private Context context;


    public StoryAdapter(Context context, List<Story> stories) {
        this.stories = stories;
        this.context = context;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.story_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, final int position) {
        holder.bindData(stories.get(position));
        holder.setonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailViewActivity.class);
                intent.putExtra(Constants.EXTRA_STORY_ID, stories.get(position).getId());
                intent.putExtra(Constants.EXTRA_POSITION, position);
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        ImageView userProfileImageView;
        Button followToggleButton;
        TextView userNameTextView;
        TextView titleTextView;
        ImageView storyContentImageView;
        TextView descriptionTextView;
        Button likeButton;
        Button commentsButton;

        public StoryViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            userProfileImageView = (ImageView) rootView.findViewById(R.id.user_image_view);
            followToggleButton = (Button) rootView.findViewById(R.id.follow_unfollow_text_view);
            userNameTextView = (TextView) rootView.findViewById(R.id.user_name_text_view);
            titleTextView = (TextView) rootView.findViewById(R.id.story_title);
            storyContentImageView = (ImageView) rootView.findViewById(R.id.story_content_image);
            descriptionTextView = (TextView) rootView.findViewById(R.id.description_textview);
            likeButton = (Button) rootView.findViewById(R.id.like_button);
            commentsButton = (Button) rootView.findViewById(R.id.comment_button);
        }

        public void bindData(final Story story) {
            final Context context = userProfileImageView.getContext();
            User user = DataManager.getInstance().getUser(story.getDb());
            Picasso.with(userProfileImageView.getContext())
                    .load(user.getImage())
                    .resize(40, 40)
                    .into(userProfileImageView);
            Picasso.with(storyContentImageView.getContext())
                    .load(story.getSi())
                    .into(storyContentImageView);
            setupFollowing(user);
            userNameTextView.setText(user.getUsername());
            titleTextView.setText(story.getTitle());
            descriptionTextView.setText(story.getDescription());
            String comment = context.getResources().getQuantityString(R.plurals.comment_count,
                    story.getCommentCount(), story.getCommentCount());
            commentsButton.setText(comment);

            setupLikeListener(story, context);
            setupCommentListener();
            updateLikeButtonState(story, context);
        }

        private void setupCommentListener() {
            commentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), R.string.comments_error_message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setupLikeListener(final Story story, final Context context) {
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (story.isLikeFlag()) {
                        story.setLikeFlag(false);
                        story.setLikesCount(story.getLikesCount() - 1);
                    } else {
                        story.setLikeFlag(true);
                        story.setLikesCount(story.getLikesCount() + 1);
                    }
                    updateLikeButtonState(story, context);
                }
            });
        }

        private void updateLikeButtonState(Story story, Context context) {
            String like = context.getResources().getQuantityString(R.plurals.like_count,
                    story.getLikesCount(), story.getLikesCount());
            likeButton.setText(like);
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

        private void setupFollowing(final User user) {
            final int messageId;
            messageId = setState(user);
            followToggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext()).setMessage(messageId).
                            setTitle(R.string.alert).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (user.is_following()) {
                                user.setIs_following(false);
                                user.setFollowers(user.getFollowers() - 1);
                            } else {
                                user.setIs_following(true);
                                user.setFollowers(user.getFollowers() + 1);
                            }
                            dialog.dismiss();
                            setState(user);
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
            setState(user);
        }

        private int setState(User user) {
            int messageId;
            if (user.is_following()) {
                messageId = R.string.alert_message_unfollow;
                followToggleButton.setText(followToggleButton.getContext().getString(R.string.following));
            } else {
                messageId = R.string.alert_message_follow;
                followToggleButton.setText(followToggleButton.getContext().getString(R.string.unfollow));
            }
            return messageId;
        }

        public void setonClickListener(View.OnClickListener onClickListener) {
            rootView.setOnClickListener(onClickListener);
        }
    }
}
