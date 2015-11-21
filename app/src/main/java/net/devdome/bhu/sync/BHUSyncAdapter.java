package net.devdome.bhu.sync;

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

import net.devdome.bhu.Config;
import net.devdome.bhu.authentication.AccountConfig;
import net.devdome.bhu.db.NewsDatabase;
import net.devdome.bhu.rest.RestClient;
import net.devdome.bhu.rest.model.Post;
import net.devdome.bhu.rest.service.ApiService;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BHUSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String ACTION_FINISHED_SYNC = "net.devdome.bhu.sync.ACTION_FINISHED_SYNC";
    private final AccountManager accountManager;
    private Context context;

    public BHUSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        accountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(Config.TAG, "Performing Sync Operations");
        String authToken = getAuthToken(account);
        NewsDatabase db = new NewsDatabase(this.context);
        ApiService apiService = new RestClient().getApiService();
        long timestamp = db.latestTimestamp();
        Log.i(Config.TAG, "Latest post sync timestamp: " + timestamp);
        apiService.getPosts(authToken, timestamp, new Callback<Post>() {
            @Override
            public void success(Post posts, Response response) {
                List<Post.Data> data = posts.getData();
                Log.i(Config.TAG, "No. of Posts downloaded: " + data.size());
                Post.Data post;
                ContentValues values;
                NewsDatabase db = new NewsDatabase(context);
                for (int i = 0; i < data.size(); i++) {
                    post = data.get(i);
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
                    db.insert(values);
                }

                context.sendBroadcast(new Intent(ACTION_FINISHED_SYNC).putExtra(Config.EXTRA_POSTS_ADDED, true));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
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
}
