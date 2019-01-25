package com.hitstreamr.hitstreamrbeta;

public class Comment {

    private String username, message, userID, timePosted, commentID, photoURI;

    /**
     * Constructor #1
     */
    public Comment(String username, String message, String userID, String timePosted, String commentID) {
        this.username = username;
        this.message = message;
        this.userID = userID;
        this.timePosted = timePosted;
        this.commentID = commentID;
    }

    /**
     * Constructor #2
     */
    public Comment() { }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public String getCommentID() { return commentID; }

    public void setCommentID(String commentID) { this.commentID = commentID; }

    public String getTimePosted() { return timePosted; }

    public void setTimePosted(String timePosted) { this.timePosted = timePosted; }

    @Override
    public String toString() {
        return "Comment{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", userID='" + userID + '\'' +
                ", timePosted='" + timePosted + '\'' +
                ", commentID='" + commentID + '\'' +
                ", photoURI='" + photoURI + '\'' +
                '}';
    }
}
