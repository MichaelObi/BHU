package net.devdome.bhu.app.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BHUAuthenticateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        BHUAuthenticator authenticator = new BHUAuthenticator(this);
        return authenticator.getIBinder();
    }
}
