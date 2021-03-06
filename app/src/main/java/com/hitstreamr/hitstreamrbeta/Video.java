package com.hitstreamr.hitstreamrbeta;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Video implements Parcelable {
    private String title;
    private String description;
    private String genre;
    private String subGenre;
    private String privacy;
    private String url;
    private String thumbnailUrl;
    private String userId;
//    private String username;
    private String duration;
    private int pubYear;
    private String videoId;
    private Timestamp timestamp;
    private long views;
    private String delete;

    private ArrayList<Contributor> contributors;


    public Video(){
        //needed for the Firestore
    }

    public Video(String title, String description, String genre, String subGenre, String privacy, String url, String userId, String duration,
                  String thumbnailUrl, int pubYear, ArrayList<Contributor> contributors, String videoId, Timestamp timestamp,
                 long views, String delete)
    //String username should go between duration and Thumbnail
    //String username
    {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.subGenre = subGenre;
        this.privacy = privacy;
        this.url = url;
        this.userId = userId;
 //       this.username = username;
        this.thumbnailUrl = thumbnailUrl;
        this.contributors = contributors;
        this.pubYear = pubYear;
        this.duration = duration;
        this.videoId = videoId;
        this.timestamp=timestamp;
        this.views = views;
        this.delete= delete;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSubGenre() {
        return subGenre;
    }

    public void setSubGenre(String subGenre) {
        this.subGenre = subGenre;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(ArrayList<Contributor>contributors) {
        this.contributors = contributors;
    }

 //   public String getUsername() { return username; }

 //   public void setUsername(String username) {
 //       this.username = username;
//    }


    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getPubYear() {
        return pubYear;
    }

    public void setPubYear(int pubYear) {
        this.pubYear = pubYear;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return title + " " + description + " " + genre + " " + subGenre + " " + privacy + " " + url + thumbnailUrl + " " +userId;
        //+ " " + username;

    }
    protected Video(Parcel in) {
        title = in.readString();
        description = in.readString();
        genre = in.readString();
        subGenre = in.readString();
        privacy = in.readString();
        url = in.readString();
        thumbnailUrl = in.readString();
        userId = in.readString();
 //       username = in.readString();
        pubYear = in.readInt();
        videoId = in.readString();
        duration = in.readString();
        long tmpDate = in.readLong();
        timestamp = tmpDate == Long.MIN_VALUE ? null : new Timestamp(new Date(tmpDate));
        views = in.readLong();
        if (in.readByte() == 0x01) {
            contributors = new ArrayList<Contributor>();
            in.readList(contributors, Contributor.class.getClassLoader());
        } else {
            contributors = null;
        }
        delete = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(genre);
        dest.writeString(subGenre);
        dest.writeString(privacy);
        dest.writeString(url);
        dest.writeString(thumbnailUrl);
        dest.writeString(userId);
 //       dest.writeString(username);
        dest.writeInt(pubYear);
        dest.writeString(videoId);
        dest.writeString(duration);
        dest.writeLong(timestamp != null ? timestamp.toDate().getTime() : Long.MIN_VALUE);
        dest.writeLong(views);
        if (contributors == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(contributors);
        }
        dest.writeString(delete);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
