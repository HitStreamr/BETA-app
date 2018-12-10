package com.hitstreamr.hitstreamrbeta;

import android.os.Parcel;
import android.os.Parcelable;

public class Feed implements Parcelable {
    private String videoId, Repost, Like;

    public Feed() {
    }

    public Feed(String videoId, String Repost, String Like) {
        this.videoId = videoId;
        this.Repost = Repost;
        this.Like = Like;
    }

    public String getFeedvideoId() {
        return videoId;
    }

    public String getFeedRepost() {
        return Repost;
    }

    public String getFeedLike() {
        return Like;
    }

    public void setFeedvideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setFeedRepost(String Repost) {
        this.Repost = Repost;
    }

    public void setFeedLike(String Like) {
        this.Like = Like;
    }

    @Override
    public String toString() {
        return videoId + Repost + Like;
    }

    protected Feed(Parcel in) {
        videoId = in.readString();
        Repost = in.readString();
        Like = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoId);
        dest.writeString(Repost);
        dest.writeString(Like);
    }

    public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

}
