package net.devdome.bhu.app.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

import net.devdome.bhu.app.RegistrationIntentService;

public class BhuInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
