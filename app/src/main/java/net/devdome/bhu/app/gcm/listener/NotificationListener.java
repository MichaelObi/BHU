package net.devdome.bhu.app.gcm.listener;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Notification;
import net.devdome.bhu.app.gcm.GCMTaskListener;
import net.devdome.bhu.app.ui.activity.NotificationActivity;

import io.realm.Realm;

public class NotificationListener extends GCMTaskListener {
    private static final String TAG = "NotificationListener";

    @Override
    public void handle(Context context, String type, String payload) {
        Gson gson = new Gson();
        NotificationData notificationData;
        try {
            notificationData = gson.fromJson(payload, NotificationData.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse notification data");
            return;
        }

        final String title = TextUtils.isEmpty(notificationData.subject) ?
                context.getString(R.string.app_name) : notificationData.subject;
        final String message = TextUtils.isEmpty(notificationData.message) ? "" : notificationData.message;

        Intent intent = new Intent(context, NotificationActivity.class).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Notification notification = new Notification();
        notification.setSubject(notificationData.subject);
        notification.setMessage(notificationData.message);
        notification.setCreated_at(notificationData.created_at);
        notification.setRead(false);

        realm.copyToRealm(notification);
        realm.commitTransaction();

        realm.close();
        // fire the notification
        fireNotification(context, title, message, intent);
    }

    private static class NotificationData {
        String subject;
        String message;
        long created_at;
    }
}
