package net.devdome.bhu.rest.service;

import net.devdome.bhu.rest.model.Course;
import net.devdome.bhu.rest.model.Post;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ApiService {
    @GET("/posts")
    void getPosts(@Header("X-Authorization") String authCode, @Query("latest_update") long updatedAt, Callback<Post> callback);

    @GET("/courses/all")
    void getAllCourses(@Header("X-Authorization") String authCode, Callback<Course> callback);

    @POST("/courses/register")
    @FormUrlEncoded
    void registerCourses(@Header("X-Authorization") String authCode, @Field("course_codes[]") List<String> courses, Callback<Course> cb);
}
