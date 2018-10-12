package com.hitstreamr.hitstreamrbeta;

import java.util.ArrayList;

public class Video {
    private String title;
    private String description;
    private String genre;
    private String subGenre;
    private String privacy;
    private String url;
    private String thumbnailUrl;
    private String userId;
    private String username;
    private int pubYear;

    private ArrayList<Contributor> contributors;


    public Video(){
        //needed for the Firestore
    }

    public Video(String title, String description, String genre, String subGenre, String privacy, String url, String userId, String username, String thumbnailUrl, int pubYear, ArrayList<Contributor> contributors) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


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

    @Override
    public String toString() {
        return title + " " + description + " " + genre + " " + subGenre + " " + privacy + " " + url + " " +userId + " " + username;

    }
}

