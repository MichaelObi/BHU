package net.devdome.bhu.app.gcm;

import com.google.android.gms.gcm.GcmListenerService;

import android.os.Bundle;
import android.util.Log;

import net.devdome.bhu.app.gcm.listener.NotificationListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BhuGcmListenerService extends GcmListenerService {
    public static final String KEY_EVENT = "event";
    private static final String TAG = "BhuGcmListenerService";

    private static final Map<String, GCMTaskListener> TASK_LISTENERS;

    static {
        Map<String, GCMTaskListener> listeners = new HashMap<String, GCMTaskListener>();
        listeners.put("notification", new NotificationListener());
        listeners.put("post_published", new NotificationListener());
        listeners.put("post_unpublished", new NotificationListener());
        TASK_LISTENERS = Collections.unmodifiableMap(listeners);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String event = data.getString(KEY_EVENT);
        Log.i(TAG, "GCM Event: " + event);
        if (event == null) {
            Log.e(TAG, "Message received without event.");
            return;
        }

        event = event.toLowerCase();
        GCMTaskListener taskListener = TASK_LISTENERS.get(event);
        if (taskListener == null) {
            Log.e(TAG, "An unknown event was received.");
            return;
        }

        taskListener.handle(this, event, data.getString("data"));

    }
}
