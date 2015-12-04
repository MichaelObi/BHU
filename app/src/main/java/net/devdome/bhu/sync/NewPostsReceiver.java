package net.devdome.bhu.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.ui.activity.MainActivity;

public class NewPostsReceiver extends BroadcastReceiver {
    public static final int mId = 200;

    public NewPostsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(Config.EXTRA_POSTS_ADDED, false)) {
            Log.i(Config.TAG, "New Posts added. Fire notification.");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New Posts published")
                            .setContentText("Fresh news articles are now available. Tap to read them now.")
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setVibrate(new long[]{1000, 500, 500, 500}).setLights(Color.BLUE, 3000, 3000);
            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(mId, mBuilder.build());
        }
    }
}
