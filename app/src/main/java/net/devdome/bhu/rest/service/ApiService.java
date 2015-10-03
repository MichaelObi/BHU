package net.devdome.bhu.rest.service;

import net.devdome.bhu.rest.model.Post;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;

public interface ApiService {
    @GET("/posts")
    void getPosts(@Header("X-Authorization") String authCode, Callback<Post> callback);
}
