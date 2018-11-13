package com.hitstreamr.hitstreamrbeta;

import java.util.ArrayList;

public class Book {

    private String title;
    private String description;
    private String genre;
    private String subGenre;
    private String privacy;
    private String url;
    private String thumbnailUrl;
    private String userId;
    private String username;
    private String duration;
    private int pubYear;
    private String videoId;

    private ArrayList<Contributor> contributors;

    public Book(String title, String description, String genre, String subGenre, String privacy, String url, String userId, String duration,
                 String username, String thumbnailUrl, int pubYear, ArrayList<Contributor> contributors) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.subGenre = subGenre;
        this.privacy = privacy;
        this.url = url;
        this.userId = userId;
        this.username = username;
        this.thumbnailUrl = thumbnailUrl;
        this.contributors = contributors;
        this.pubYear = pubYear;
        this.duration = duration;
        this.videoId = null;
    }

    public ArrayList<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(ArrayList<Contributor> contributors) {
        this.contributors = contributors;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

}