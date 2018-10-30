package com.hitstreamr.hitstreamrbeta.UserTypes;

public class UsernameUserIdPair {

    protected String username, tempUserId;

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


    public UsernameUserIdPair(String username, String tempUserId) {
        this.username = username;
        this.tempUserId = tempUserId;
    }

}