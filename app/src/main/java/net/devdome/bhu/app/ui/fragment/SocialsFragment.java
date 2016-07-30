package net.devdome.bhu.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.devdome.bhu.app.R;

public class SocialsFragment extends Fragment {
    private static final String SEARCH_QUERY = "#BinghamUni AND pic.twitter.com";

    public SocialsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_social, container, false);

        return v;
    }


}
