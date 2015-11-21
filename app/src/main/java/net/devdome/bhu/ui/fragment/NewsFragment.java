package net.devdome.bhu.ui.fragment;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.authentication.AccountConfig;
import net.devdome.bhu.provider.NewsProvider;
import net.devdome.bhu.sync.BHUSyncAdapter;
import net.devdome.bhu.ui.adapter.NewsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private static IntentFilter intentFilter = new IntentFilter(BHUSyncAdapter.ACTION_FINISHED_SYNC);
    Context context;
    AccountManager accountManager;
    View view;
    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.e(Config.TAG, "Broadcast: posts updated! ");
            if (intent.getBooleanExtra(Config.EXTRA_POSTS_ADDED, false)) {
                final Snackbar snackbar = Snackbar.make(view, "New articles have been added", Snackbar.LENGTH_LONG)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Snackbar action clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                snackbar.show();
            }
        }
    };


    public NewsFragment() {
    }

    public static NewsFragment getInstance() {
        return new NewsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView rvNews = (RecyclerView) view.findViewById(R.id.rv_news);
        rvNews.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvNews.setLayoutManager(layoutManager);
        rvNews.setAdapter(new NewsAdapter(context));
        setHasOptionsMenu(true);
        accountManager = AccountManager.get(context);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        Account account = accountManager.getAccountsByType(AccountConfig.ACCOUNT_TYPE)[0];
        if (ContentResolver.isSyncPending(account, NewsProvider.AUTHORITY) ||
                ContentResolver.isSyncActive(account, NewsProvider.AUTHORITY)) {
            Log.i(Config.TAG, "ContentResolver: SyncPending, canceling");
            ContentResolver.cancelSync(account, NewsProvider.AUTHORITY);
        }
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, NewsProvider.AUTHORITY, settingsBundle);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(syncBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(syncBroadcastReceiver, intentFilter);
    }
}

