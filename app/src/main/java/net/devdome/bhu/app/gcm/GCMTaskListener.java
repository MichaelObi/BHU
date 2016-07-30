package net.devdome.bhu.app.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import net.devdome.bhu.app.R;

public abstract class GCMTaskListener {
    public abstract void handle(Context context, String type, String data);

    protected void fireNotification(Context context, String title, String message, Intent intent) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, new NotificationCompat.Builder(context)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_notifications_none_24dp)
                        .setTicker(message)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setColor(ContextCompat.getColor(context, R.color.primary))
                        .setContentIntent(PendingIntent.getActivity(context, 0, intent,
                                PendingIntent.FLAG_CANCEL_CURRENT))
                        .setAutoCancel(true)
                        .build());
    }
}
