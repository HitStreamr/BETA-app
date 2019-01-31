package com.hitstreamr.hitstreamrbeta.UserTypes;

public class User {
    private String username, email, userID, fullname, bio, verified;

    public User() { }

    public User(String username, String email, String userID, String fullname, String bio/*, String verified*/) {
        this.username = username;
        this.email = email;
        this.userID = userID;
        this.fullname = fullname;
        this.bio = bio;
//        this.verified = verified;
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

//    public String getVerified() {
//        return verified;
//    }
//
//    public void setVerified(String verified) {
//        this.verified = verified;
//    }
}
