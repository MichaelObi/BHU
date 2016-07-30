package net.devdome.bhu.app.model;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Comment {
    public String id;
    public String comment;
    public long createdAt;
    public ProfileIndex user;

    public Comment() {
    }

    public Comment(String comment, ProfileIndex user) {
        this.comment = comment;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public ProfileIndex getUser() {
        return user;
    }

    public void setUser(ProfileIndex user) {
        this.user = user;
    }

}