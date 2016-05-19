package net.devdome.bhu.app.gcm.listener;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.os.Bundle;
import android.util.Log;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.authentication.AccountConfig;
import net.devdome.bhu.app.gcm.GCMTaskListener;
import net.devdome.bhu.app.provider.NewsProvider;

public class PostPublishedListener extends GCMTaskListener {
    @Override
    public void handle(Context context, String event, String data) {
        AccountManager accountManager = AccountManager.get(context);

        accountManager = AccountManager.get(context);
        Account account = accountManager.getAccountsByType(AccountConfig.ACCOUNT_TYPE)[0];
        if (ContentResolver.isSyncPending(account, NewsProvider.AUTHORITY) ||
                ContentResolver.isSyncActive(account, NewsProvider.AUTHORITY)) {
            Log.i(Config.TAG, "ContentResolver: SyncPending, canceling");
            ContentResolver.cancelSync(account, NewsProvider.AUTHORITY);
        }
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        SyncRequest.Builder srBuilder = new SyncRequest.Builder();
        srBuilder.setSyncAdapter(account, NewsProvider.AUTHORITY)
                .setExpedited(true).setExtras(settingsBundle)
                .syncOnce();
        ContentResolver.requestSync(srBuilder.build());
    }
}
