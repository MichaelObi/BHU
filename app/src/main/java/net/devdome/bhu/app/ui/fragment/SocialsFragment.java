package net.devdome.bhu.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import net.devdome.bhu.app.R;

public class SocialsFragment extends Fragment {
    private static final String SEARCH_QUERY = "#BinghamUni AND pic.twitter.com";

    public SocialsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_social, container, false);
        ListView lv = (ListView) v.findViewById(R.id.tweet_list);
        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(SEARCH_QUERY).build();

        final TweetTimelineListAdapter timelineAdapter = new TweetTimelineListAdapter(getActivity(), searchTimeline);
        lv.setAdapter(timelineAdapter);
        lv.setEmptyView(v.findViewById(R.id.loading));
        return v;
    }


}
