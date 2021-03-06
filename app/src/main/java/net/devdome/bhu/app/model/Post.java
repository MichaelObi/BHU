package net.devdome.bhu.app.model;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import net.devdome.bhu.app.db.NewsDatabase;

import java.util.ArrayList;
import java.util.List;

public class Post {

    int id, postId, authorId;
    long createdAt, updatedAt;
    String postTitle, postContent, postContentHtml, featuredImageLink, authorName;

    public static List<Post> getPostList(Cursor cursor) {
        List<Post> posts = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                posts.add(getPost(cursor));
            } while (cursor.moveToNext());
        }
        return posts;
    }

    @NonNull
    public static Post getPost(Cursor cursor) {
        Post post = new Post();
        post.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        post.postId = cursor.getInt(cursor.getColumnIndex(NewsDatabase.KEY_POST_ID));
        post.authorId = cursor.getInt(cursor.getColumnIndex(NewsDatabase.KEY_POST_AUTHOR_ID));
        post.createdAt = cursor.getLong(cursor.getColumnIndex(NewsDatabase.KEY_CREATED_AT));
        post.updatedAt = cursor.getLong(cursor.getColumnIndex(NewsDatabase.KEY_UPDATED_AT));
        post.postTitle = cursor.getString(cursor.getColumnIndex(NewsDatabase.KEY_POST_TITLE));
        post.postContent = cursor.getString(cursor.getColumnIndex(NewsDatabase.KEY_POST_CONTENT));
        post.postContentHtml = cursor.getString(cursor.getColumnIndex(NewsDatabase.KEY_POST_CONTENT_HTML));
        post.featuredImageLink = cursor.getString(cursor.getColumnIndex(NewsDatabase.KEY_POST_FEATURED_IMAGE));
        post.authorName = cursor.getString(cursor.getColumnIndex(NewsDatabase.KEY_POST_AUTHOR_NAME));
        return post;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostContentHtml() {
        return postContentHtml;
    }

    public void setPostContentHtml(String postContentHtml) {
        this.postContentHtml = postContentHtml;
    }

    public String getFeaturedImageLink() {
        return featuredImageLink;
    }

    public void setFeaturedImageLink(String featuredImageLink) {
        this.featuredImageLink = featuredImageLink;
    }
}
