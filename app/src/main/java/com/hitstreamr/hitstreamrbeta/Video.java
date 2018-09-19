package com.hitstreamr.hitstreamrbeta;

public class Video {
    private String title, description, genre, subGenre, privacy, url, userId;

    public Video(){
        //needed for the Firestore
    }

    public Video(String title, String description, String genre, String subGenre, String privacy, String url, String userId) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.subGenre = subGenre;
        this.privacy = privacy;
        this.url = url;
        this.userId = userId;
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

    @Override
    public String toString() {
        return title + " " + description + " " + genre + " " + subGenre + " " + privacy + " " + url + " " +userId;

    }
}

