package com.hitstreamr.hitstreamrbeta;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Playlist implements Parcelable {
    public String playlistname;
    public ArrayList<Video> playVideos;
    public ArrayList<String> playVideoIds;
    public String playThumbnails;

    public Playlist(String playlistname, ArrayList<Video> playVideos, ArrayList<String> playVideoIds,String playThumbnails) {
        this.playlistname = playlistname;
        this.playVideos = playVideos;
        this.playVideoIds = playVideoIds;
        this.playThumbnails = playThumbnails;
    }

    public Playlist() {
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };


    public String getPlayThumbnails() {
        return playThumbnails;
    }

    public void setPlayThumbnails(String playThumbnails) {
        this.playThumbnails = playThumbnails;
    }

    public String getPlaylistname() {
        return playlistname;
    }

    public void setPlaylistname(String playlistname) {
        this.playlistname = playlistname;
    }

    public ArrayList<Video> getPlayVideos() {
        return playVideos;
    }

    public void setPlayVideos(ArrayList<Video> playVideos) {
        this.playVideos = playVideos;
    }

    public ArrayList<String> getPlayVideoIds() {
        return playVideoIds;
    }

    public void setPlayVideoIds(ArrayList<String> playVideoIds) {
        this.playVideoIds = playVideoIds;
    }

    protected Playlist(Parcel in) {
        playlistname = in.readString();

        if (in.readByte() == 0x01) {
            playVideos = new ArrayList<Video>();
            in.readList(playVideos, Video.class.getClassLoader());
        } else {
            playVideos = null;
        }
        if (in.readByte() == 0x01) {
            playVideoIds = new ArrayList<String>();
            in.readList(playVideoIds, String.class.getClassLoader());
        } else {
            playVideoIds = null;
        }

        playThumbnails = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(playlistname);
        if (playVideos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(playVideos);
        }

        if (playVideoIds == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(playVideoIds);
        }

        dest.writeString(playThumbnails);

    }

}
