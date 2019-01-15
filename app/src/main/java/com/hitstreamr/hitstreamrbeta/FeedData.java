package com.hitstreamr.hitstreamrbeta;

import com.google.firebase.Timestamp;

public class FeedData {
    public String userId, videoId, type;
    Timestamp time;

    public FeedData(String userId, String videoId, String type, Timestamp time) {
        this.userId = userId;
        this.videoId = videoId;
        this.type = type;
        this.time = time;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
