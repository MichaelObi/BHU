package net.devdome.bhu.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import net.devdome.bhu.Config;
import net.devdome.bhu.authentication.AccountConfig;
import net.devdome.bhu.provider.NewsProvider;

public class BHUSyncService extends Service {

    public static final Object syncAdapterLock = new Object();
    public static BHUSyncAdapter syncAdapter = null;
    private static IntentFilter intentFilter = new IntentFilter(BHUSyncAdapter.ACTION_FINISHED_SYNC);
    AccountManager accountManager;

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
            syncAdapter = new BHUSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
