package com.hitstreamr.hitstreamrbeta.UserTypes;

import com.google.firebase.auth.FirebaseUser;

public class UsernameUserIdPair {

    protected String username, tempUserId;
    FirebaseUser tempUser;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTempUserId() {
        return tempUserId;
    }

    public void setTempUserId(String tempUserId) {
        this.tempUserId = tempUserId;
    }

    public FirebaseUser getTempUser() {
        return tempUser;
    }

    public void setTempUser(FirebaseUser tempUser) {
        this.tempUser = tempUser;
    }

    public UsernameUserIdPair(String username, String tempUserId, FirebaseUser tempUser) {
        this.username = username;
        this.tempUserId = tempUserId;
        this.tempUser = tempUser;
    }

}
