package net.devdome.bhu.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BHUSyncService extends Service {

    public static final Object syncAdapterLock = new Object();
    public static BHUSyncAdapter syncAdapter = null;
    //    private static IntentFilter intentFilter = new IntentFilter(BHUSyncAdapter.ACTION_FINISHED_SYNC);
//    AccountManager accountManager;

    public BHUSyncService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // SYNC
//        accountManager = AccountManager.get(getApplicationContext());
//        Account account = accountManager.getAccountsByType(AccountConfig.ACCOUNT_TYPE)[0];
//        if (ContentResolver.isSyncPending(account, NewsProvider.AUTHORITY) ||
//                ContentResolver.isSyncActive(account, NewsProvider.AUTHORITY)) {
//            Log.i(Config.TAG, "ContentResolver: SyncPending, canceling");
//            ContentResolver.cancelSync(account, NewsProvider.AUTHORITY);
//        }
//        Bundle settingsBundle = new Bundle();
//        settingsBundle.putBoolean(
//                ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settingsBundle.putBoolean(
//                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.addPeriodicSync(account, NewsProvider.AUTHORITY, settingsBundle, Config.SYNC_PERIOD);

        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new BHUSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
