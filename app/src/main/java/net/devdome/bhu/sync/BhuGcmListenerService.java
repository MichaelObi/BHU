package net.devdome.bhu.sync;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class BhuGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

    }
}
