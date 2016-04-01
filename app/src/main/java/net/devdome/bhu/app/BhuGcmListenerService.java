package net.devdome.bhu.app;

import com.google.android.gms.gcm.GcmListenerService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import net.devdome.bhu.app.authentication.AccountConfig;
import net.devdome.bhu.app.provider.NewsProvider;
import net.devdome.bhu.app.ui.activity.MainActivity;

public class BhuGcmListenerService extends GcmListenerService {
    public static final String EVENT_POST_PUBLISHED = "post_published";
    public static final String EVENT_POST_UNPUBLISHED = "post_unpublished";
    public static final String KEY_EVENT = "event";
    private static final String TAG = "BhuGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
//        sendNotification(from);
        Log.i(TAG, "You looking at my Logs? You played yourself. Bless up!");
        Log.i(TAG, "GCM Message from: " + from);
        Log.i(TAG, "GCM Event: " + data.getString(KEY_EVENT));
        String event = data.getString(KEY_EVENT);

        assert event != null;
        if (event.equalsIgnoreCase(EVENT_POST_PUBLISHED) || event.equalsIgnoreCase(EVENT_POST_UNPUBLISHED)) {
            handlePostsPublished();
        }
    }

    private void handlePostsPublished() {
        AccountManager accountManager = AccountManager.get(this);

        accountManager = AccountManager.get(this);
        Account account = accountManager.getAccountsByType(AccountConfig.ACCOUNT_TYPE)[0];
        if (ContentResolver.isSyncPending(account, NewsProvider.AUTHORITY) ||
                ContentResolver.isSyncActive(account, NewsProvider.AUTHORITY)) {
            Log.i(Config.TAG, "ContentResolver: SyncPending, canceling");
            ContentResolver.cancelSync(account, NewsProvider.AUTHORITY);
        }
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        SyncRequest.Builder srBuilder = new SyncRequest.Builder();
        srBuilder.setSyncAdapter(account, NewsProvider.AUTHORITY)
                .setExpedited(true).setExtras(settingsBundle)
                .syncOnce();
        ContentResolver.requestSync(srBuilder.build());
    }


    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_new)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
