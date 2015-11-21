package net.devdome.bhu.sync;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;

import net.devdome.bhu.Config;

public class BhuGcmListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.e(Config.TAG, "GCM Message recieved from: " + from);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentText("GCM Message recieved from: " + from);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(22, notification);
    }
}
