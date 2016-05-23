package com.roposo.assignment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roposo.assignment.data.DataManager;
import com.roposo.assignment.data.Story;
import com.roposo.assignment.data.User;
import com.squareup.picasso.Picasso;

public class DetailViewActivity extends AppCompatActivity {


    private User user;
    private Story story;

    ImageView userProfileImageView;
    TextView followToggleTextView;
    TextView userNameTextView;
    TextView followersCount;
    Button likeButton;
    Button commentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView descriptionTextView = (TextView) findViewById(R.id.description_textview);
        likeButton = (Button) findViewById(R.id.like_button);
        commentsButton = (Button) findViewById(R.id.comment_button);
        ImageView storyContentImageView = (ImageView) findViewById(R.id.backdrop);
        TextView titleTextView = (TextView) findViewById(R.id.story_title);
        userProfileImageView = (ImageView) findViewById(R.id.user_image_view);
        followToggleTextView = (TextView) findViewById(R.id.follow_unfollow_text_view);
        userNameTextView = (TextView) findViewById(R.id.user_name_text_view);
        followersCount = (TextView) findViewById(R.id.followers_count);
        String storyId = getIntent().getStringExtra(Constants.EXTRA_STORY_ID);
        story = DataManager.getInstance().getStoryById(storyId);
        user = DataManager.getInstance().getUser(story.getDb());

        Picasso.with(this).load(story.getSi()).fit().into(storyContentImageView);
        Picasso.with(this).load(user.getImage()).into(userProfileImageView);
        descriptionTextView.setText(story.getDescription());
        titleTextView.setText(story.getTitle());
        updateLikeButtonState();
        setupFollowing();
        setupLike();
        setupComment();
    }

    private void setupFollowing() {
        final int messageId;
        messageId = setFollowerState();
        followToggleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailViewActivity.this).setMessage(messageId).
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
                        setFollowerState();
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    private int setFollowerState() {
        followersCount.setText(String.valueOf(user.getFollowers()));
        int messageId;
        if (user.is_following()) {
            messageId = R.string.alert_message_unfollow;
            followToggleTextView.setText(followToggleTextView.getContext().getString(R.string.following));
        } else {
            messageId = R.string.alert_message_follow;
            followToggleTextView.setText(followToggleTextView.getContext().getString(R.string.unfollow));
        }
        return messageId;
    }

    private void setupComment() {
        String comment = getResources().getQuantityString(R.plurals.comment_count,
                story.getCommentCount(), story.getCommentCount());
        commentsButton.setText(comment);

        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.comments_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLike() {
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
                updateLikeButtonState();
            }
        });
    }


    private void updateLikeButtonState() {
        String like = getResources().getQuantityString(R.plurals.like_count,
                story.getLikesCount(), story.getLikesCount());
        likeButton.setText(like);
        if (story.isLikeFlag()) {
            Drawable drawable = getDrawable(this, R.drawable.ic_favorite_24dp);
            likeButton.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = getDrawable(this, R.drawable.ic_favorite_border_black_24dp);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_POSITION, getIntent().getIntExtra(Constants.EXTRA_POSITION, -1));
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
