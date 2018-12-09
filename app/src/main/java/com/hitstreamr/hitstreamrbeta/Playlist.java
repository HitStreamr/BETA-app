package com.hitstreamr.hitstreamrbeta;

import java.util.ArrayList;

public class Playlist {
    public String playlistname;
    public ArrayList<String> playVideos;

    public Playlist(String playlistname, ArrayList<String> playVideos) {
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

    public ArrayList<String> getPlayVideos() {
        return playVideos;
    }

    public void setPlayVideos(ArrayList<String> playVideos) {
        this.playVideos = playVideos;
    }
}

