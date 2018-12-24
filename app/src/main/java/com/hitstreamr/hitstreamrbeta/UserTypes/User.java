package com.hitstreamr.hitstreamrbeta.UserTypes;

import java.util.ArrayList;

public class User {
    private String username, email, userID;
    private ArrayList<String> genres;
    public User() { }

    public User(String username, String email, String userID) {
        this.username = username;
        this.email = email;
        this.userID = userID;
        this.genres = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }
}
