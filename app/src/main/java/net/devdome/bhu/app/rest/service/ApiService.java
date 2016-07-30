package net.devdome.bhu.app.rest.service;

import net.devdome.bhu.app.rest.model.Avatar;
import net.devdome.bhu.app.rest.model.Blank;
import net.devdome.bhu.app.rest.model.Course;
import net.devdome.bhu.app.rest.model.CurricularEvent;
import net.devdome.bhu.app.rest.model.Post;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface ApiService {
    @POST("/devices/add")
    @FormUrlEncoded
    void sendGcmTokenToServer(@Header("X-Authorization") String authCode, @Field("device_token") String token, Callback<Blank> callback);

    @GET("/posts")
    void getPosts(@Header("X-Authorization") String authCode, @Query("latest_update") long updatedAt, Callback<Post> callback);

    /*Course Registration Routes*/

    @GET("/courses/all")
    void getAllCourses(@Header("X-Authorization") String authCode, Callback<Course> callback);

    @POST("/courses/register")
    @FormUrlEncoded
    void registerCourses(@Header("X-Authorization") String authCode, @Field("course_codes[]") List<String> courses, Callback<Course> cb);
    /* End Course Registration Routes*/

    @GET("/classes")
    void getCurricEvents(@Header("X-Authorization") String authCode, Callback<CurricularEvent> callback);


    @Multipart
    @POST("/users/avatar/upload")
    void uploadAvatar(@Header("X-Authorization") String authCode, @Part("avatar") TypedFile image, Callback<Avatar> callback);
}
