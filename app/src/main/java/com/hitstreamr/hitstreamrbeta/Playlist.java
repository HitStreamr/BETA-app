package com.hitstreamr.hitstreamrbeta;

import java.util.ArrayList;

public class Playlist {
    public String playlistname;
    public ArrayList<Video> playVideos;

    public Playlist(String playlistname, ArrayList<Video> playVideos) {
        this.playlistname = playlistname;
        this.playVideos = playVideos;
    }

    public Playlist() {
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
}
