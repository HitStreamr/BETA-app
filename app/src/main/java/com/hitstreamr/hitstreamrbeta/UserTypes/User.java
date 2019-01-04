package com.hitstreamr.hitstreamrbeta.UserTypes;

import java.util.ArrayList;

public class User {
    private String username, email, userID, fullname, bio;
    private ArrayList<String> genres;

    public User() {     }

    public User(String username, String email, String fullname, String bio,String userID) {
        this.username = username;
        this.email = email;
        this.userID = userID;
        this.fullname = fullname;
        this.bio = bio;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
