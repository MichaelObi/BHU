package net.devdome.bhu.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.authentication.AccountConfig;
import net.devdome.bhu.app.db.NewsDatabase;
import net.devdome.bhu.app.db.realm.Course;
import net.devdome.bhu.app.db.realm.CurricEvent;
import net.devdome.bhu.app.rest.RestClient;
import net.devdome.bhu.app.rest.model.CurricularEvent;
import net.devdome.bhu.app.rest.model.Post;
import net.devdome.bhu.app.rest.service.ApiService;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BHUSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String ACTION_FINISHED_SYNC = "net.devdome.bhu.app.sync.ACTION_FINISHED_SYNC";
    private final AccountManager accountManager;
    ApiService apiService;
    Realm realm;
    private Context context;
    private String authToken;

    public BHUSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        accountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(Config.TAG, "Performing Sync Operations");
        authToken = getAuthToken(account);
        apiService = new RestClient().getApiService();
        realm = Realm.getDefaultInstance();
        syncNews();
        syncCurricularActivities();
        RealmQuery<CurricEvent> query = realm.where(CurricEvent.class);
        Log.i(Config.TAG, "No of curric events available " + String.valueOf(query.findAll().size()));
    }

    private String getAuthToken(Account account) {
        String authToken = "";
        try {
            authToken = accountManager.blockingGetAuthToken(account, AccountConfig.TOKEN_TYPE, true);
            if (authToken == null) {
                SharedPreferences preferences = context.getSharedPreferences(Config.KEY_USER_PROFILE, Context.MODE_PRIVATE);
                authToken = preferences.getString(Config.KEY_AUTH_TOKEN, null); // use sharedpreferences instead
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return authToken;
    }

    private void syncCurricularActivities() {
        Log.i(Config.TAG, "Begin sync of curric events ");

        apiService.getCurricEvents(this.authToken, new Callback<CurricularEvent>() {
                    @Override
                    public void success(CurricularEvent curricularEvent, Response response) {
                        Realm rlm = Realm.getDefaultInstance();
                        Log.i(Config.TAG, "Success with download sync curric events");

                        List<CurricularEvent.Data> curricEvents = curricularEvent.getData();
                        for (final CurricularEvent.Data event : curricEvents) {
                            rlm.beginTransaction();
                            Log.i(Config.TAG, event.getCourse().getTitle());
                            Course course = new Course();
                            course.setCode(event.getCourse().getCode());
                            course.setRegistered(true);
                            course.setTitle(event.getCourse().getTitle());
                            course = rlm.copyToRealmOrUpdate(course);

                            CurricEvent curricEvent = new CurricEvent();
                            curricEvent.setId(event.getId());
                            curricEvent.setCourse(course);
                            curricEvent.setDay(event.getDay().toUpperCase());
                            curricEvent.setDuration(event.getDuration());
                            curricEvent.setStarts_at(event.getStarts_at());
                            curricEvent.setType(event.getType());
                            curricEvent.setVenue(event.getVenue().getName());
                            rlm.copyToRealmOrUpdate(curricEvent);

                            rlm.commitTransaction();

                        }

                        Log.e(Config.TAG, "No of curric events = " + String.valueOf(rlm.where(CurricEvent.class).findAll().size()));
                        rlm.close();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(Config.TAG, "SYNC CURRIC ACTIVITIES ERROR: " + error.getMessage());
                    }
                }

        );
    }

    private void syncNews() {
        final NewsDatabase db = new NewsDatabase(this.context);
        long timestamp = db.latestTimestamp();
        Log.d(Config.TAG, "Latest post sync timestamp: " + timestamp);
        apiService.getPosts(authToken, timestamp, new Callback<Post>() {
            @Override
            public void success(final Post posts, Response response) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Post.Data> data = posts.getData();
                        Log.i(Config.TAG, "No. of Posts downloaded: " + data.size());
                        Post.Data post;
                        ContentValues values;
//                NewsDatabase db = new NewsDatabase(context);
                        boolean newPostsAdded = false;
                        for (int i = 0; i < data.size(); i++) {
                            post = data.get(i);
                            if (post.isDeleted()) {
                                db.delete(post.getId());
                                continue;
                            }

                            values = new ContentValues(data.size());
                            values.put(NewsDatabase.KEY_POST_ID, post.getId());
                            values.put(NewsDatabase.KEY_POST_TITLE, post.getTitle());
                            values.put(NewsDatabase.KEY_POST_CONTENT, post.getPost_content());
                            values.put(NewsDatabase.KEY_POST_CONTENT_HTML, post.getPost_content_html());
                            values.put(NewsDatabase.KEY_POST_FEATURED_IMAGE, post.getFeatured_image());
                            values.put(NewsDatabase.KEY_POST_AUTHOR_ID, post.getAuthor_id());
                            values.put(NewsDatabase.KEY_POST_AUTHOR_NAME, post.getAuthor_first_name() + " " + post.getAuthor_last_name());
                            values.put(NewsDatabase.KEY_CREATED_AT, post.getCreated_at() * 1000);
                            values.put(NewsDatabase.KEY_UPDATED_AT, post.getUpdated_at() * 1000);
                            if (db.findPost(post.getId()) == null) {
                                db.insert(values);
                                newPostsAdded = true;
                            } else {
                                db.update(post.getId(), values);
                            }
                        }
                        db.close();
                        context.sendBroadcast(new Intent(ACTION_FINISHED_SYNC).putExtra(Config.EXTRA_POSTS_ADDED, newPostsAdded)); //Mock newPostsAdded as true for testing
                        Log.i(Config.TAG, "New posts? " + Boolean.toString(newPostsAdded));
                    }
                }).start();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
