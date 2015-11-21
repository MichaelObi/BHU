package net.devdome.bhu.rest.service;

import net.devdome.bhu.rest.model.Post;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

public interface ApiService {
    @GET("/posts")
    void getPosts(@Header("X-Authorization") String authCode, @Query("latest_update") long updatedAt, Callback<Post> callback);

}
