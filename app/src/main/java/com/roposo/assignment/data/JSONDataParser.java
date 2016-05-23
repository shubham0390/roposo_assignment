package com.roposo.assignment.data;

import android.content.Context;
import android.util.Log;

import com.roposo.assignment.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONDataParser {

    private final String TAG = getClass().getName();

    private DataManager dataManager;

    public JSONDataParser() {
        this.dataManager = DataManager.getInstance();
    }

    public void praseData(Context context) {
        Log.d(TAG, "Parsing Data start");
        String jsonString = readFile("response_data.json", context);
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                /* If json object contain key as type than i am assuming it is a story otherwise user*/
                boolean isStory = jsonObject.has(Constants.STORY_TYPE);
                if (isStory) {
                    Log.d(TAG, "JSON object is a story at position -: " + i);
                    parseStory(jsonObject);
                } else {
                    Log.d(TAG, "JSON object is an user at position -: " + i);
                    parseUser(jsonObject);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse json string" + e.getMessage());
        }
        Log.d(TAG, "Parsing Data start");
    }

    private void parseUser(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.setCreatedOn(jsonObject.getLong(Constants.USER_CREATED_ON));
        user.setIs_following(jsonObject.getBoolean(Constants.USER_IS_FOLLOWING));
        user.setUrl(jsonObject.getString(Constants.USER_URL));
        user.setImage(jsonObject.getString(Constants.USER_IMAGE));
        user.setHandle(jsonObject.getString(Constants.USER_HANDLE));
        user.setFollowing(jsonObject.getInt(Constants.USER_FOLLOWING));
        user.setFollowers(jsonObject.getInt(Constants.USER_FOLLOWERS));
        user.setUsername(jsonObject.getString(Constants.USER_NAME));
        user.setId(jsonObject.getString(Constants.USER_ID));
        user.setAbout(jsonObject.getString(Constants.USER_ABOUT));
        dataManager.addUser(user);
    }

    private void parseStory(JSONObject jsonObject) throws JSONException {
        Story story = new Story();
        story.setCommentCount(jsonObject.getInt(Constants.STORY_COMMENT_COUNT));
        story.setLikesCount(jsonObject.getInt(Constants.STORY_LIKE_COUNT));
        story.setLikeFlag(jsonObject.getBoolean(Constants.STORY_LIKE_FLAG));
        story.setTitle(jsonObject.getString(Constants.STORY_TITLE));
        story.setType(jsonObject.getString(Constants.STORY_TYPE));
        story.setSi(jsonObject.getString(Constants.STORY_SI));
        story.setUrl(jsonObject.getString(Constants.STORY_URL));
        story.setDb(jsonObject.getString(Constants.STORY_DB));
        story.setVerb(jsonObject.getString(Constants.STORY_VERB));
        story.setId(jsonObject.getString(Constants.STORY_ID));
        story.setDescription(jsonObject.getString(Constants.STORY_DESCRIPTION));
        dataManager.addStory(story);
    }

    private String readFile(String fileName, Context context) {
        Log.d(TAG, "Read file start");
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to read file from assets " + e.getMessage());
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (IOException e2) {
                Log.e(TAG, "Unable to close streams " + e2.getMessage());
            }
        }
        Log.d(TAG, "Read file end");
        return returnString.toString();
    }
}
