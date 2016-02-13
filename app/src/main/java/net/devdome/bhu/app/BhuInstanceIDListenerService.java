package net.devdome.bhu.app;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

public class BhuInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
