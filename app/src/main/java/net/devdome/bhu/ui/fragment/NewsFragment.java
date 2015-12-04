package net.devdome.bhu.ui.fragment;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncRequest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import net.devdome.bhu.ui.components.SwipeRefreshLayout;
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
public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static IntentFilter intentFilter = new IntentFilter(BHUSyncAdapter.ACTION_FINISHED_SYNC);
    Context context;
    AccountManager accountManager;
    View view;
    RecyclerView rvNews;
    SwipeRefreshLayout swipeRefreshLayout;

    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.e(Config.TAG, "Broadcast: posts updated! ");
            swipeRefreshLayout.setRefreshing(false);
            if (intent.getBooleanExtra(Config.EXTRA_POSTS_ADDED, false)) {
                rvNews.getAdapter().notifyDataSetChanged();
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
        rvNews = (RecyclerView) view.findViewById(R.id.rv_news);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_news);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0D407F"), Color.BLACK);
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
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(syncBroadcastReceiver);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        int articleCount = rvNews.getAdapter().getItemCount();
        if (articleCount < 1 && !swipeRefreshLayout.isRefreshing()) {
            onRefresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(syncBroadcastReceiver, intentFilter);
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            return;
        }
        Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(true);
        accountManager = AccountManager.get(getActivity());
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

