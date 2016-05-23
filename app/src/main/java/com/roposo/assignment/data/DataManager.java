package com.roposo.assignment.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private static final DataManager DATA_MANAGER = new DataManager();
    private Map<String, User> userMap;
    private List<Story> storyList;

    private DataManager() {
        this.storyList = new ArrayList<>();
        this.userMap = new HashMap<>();
    }

    public static DataManager getInstance() {
        return DATA_MANAGER;
    }

    public void addUser(User user) {
        userMap.put(user.getId(), user);
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    public void addStory(Story story) {
        storyList.add(story);
    }

    public List<Story> getAllStory() {
        return storyList;
    }

    public Story getStoryById(String storyId) {
        for (Story story : storyList) {
            if (story.getId().equals(storyId)) {
                return story;
            }
        }
        return null;
    }
}
