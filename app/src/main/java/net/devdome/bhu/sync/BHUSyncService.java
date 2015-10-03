package net.devdome.bhu.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BHUSyncService extends Service {

    public static final Object syncAdapterLock = new Object();
    public static BHUSyncAdapter syncAdapter = null;

    public BHUSyncService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncAdapterLock) {
            syncAdapter = new BHUSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
