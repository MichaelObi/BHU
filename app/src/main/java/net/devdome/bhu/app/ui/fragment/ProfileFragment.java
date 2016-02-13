package net.devdome.bhu.app.ui.fragment;


import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.ui.activity.BaseActivity;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Context context;
    AccountManager accountManager;
    View view;
    TextView tvProfileTitle, tvEmailAddress, tvLevel, tvDepartment, tvMatricNo;
    ImageView imgProfile;
    Toolbar profileToolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    LinearLayout topCardContent;


    public ProfileFragment() {
    }

    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
//        ((BaseActivity) context).getSupportActionBar().hide();
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = context.getSharedPreferences(Config.KEY_USER_PROFILE, Context.MODE_PRIVATE);
        imgProfile = (ImageView) view.findViewById(R.id.img_profile);
        topCardContent = (LinearLayout) view.findViewById(R.id.profile_top_card_content);
        tvProfileTitle = (TextView) view.findViewById(R.id.profile_title);
        tvDepartment = (TextView) view.findViewById(R.id.tv_department);
        tvEmailAddress = (TextView) view.findViewById(R.id.tv_email_address);
        tvLevel = (TextView) view.findViewById(R.id.tv_level);
        tvMatricNo = (TextView) view.findViewById(R.id.tv_matric_no);

        tvDepartment.setText(String.format("%s department", prefs.getString(Config.KEY_DEPARTMENT_NAME, "Unknown")));
        tvEmailAddress.setText(prefs.getString(Config.KEY_EMAIL, "Unknown"));
        tvLevel.setText(String.format("%s level", prefs.getString(Config.KEY_LEVEL, "Unknown")));
        tvMatricNo.setText(prefs.getString(Config.KEY_MATRIC_NO, "Unknown"));



        tvProfileTitle.setText(String.format("%s %s", prefs.getString(Config.KEY_FIRST_NAME, ""), prefs.getString(Config.KEY_LAST_NAME, "")));

        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setTitle("");
//        collapsingToolbarLayout.setTitle(String.format("%s %s", prefs.getString(Config.KEY_FIRST_NAME, ""), prefs.getString(Config.KEY_LAST_NAME, "")));

        profileToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        profileToolbar.setTitle("Profile");


        String imgUrl = prefs.getString(Config.KEY_AVATAR, "http://kudago.com/static/img/default-avatar.png");
        Picasso.with(context).load(imgUrl).into(imgProfile, new Callback() {
            @Override
            public void onSuccess() {
                Palette palette = new Palette.Builder(((BitmapDrawable) imgProfile.getDrawable()).getBitmap())
                        .generate();
                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                if(!isDetached()) {
                    collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(getResources().getColor(R.color.primary_dark)));

                    if (mutedSwatch != null) {
                        collapsingToolbarLayout.setContentScrimColor(mutedSwatch.getRgb());
                    }

                    if (darkMutedSwatch != null) {
                        collapsingToolbarLayout.setExpandedTitleColor(vibrantSwatch != null ? vibrantSwatch.getTitleTextColor() : 0);
                        topCardContent.setBackgroundColor(darkMutedSwatch.getRgb());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().getWindow().setStatusBarColor(darkMutedSwatch.getRgb());
                        }
                    }
                }
            }

            @Override
            public void onError() {
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((BaseActivity) context).getSupportActionBar().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) context).getSupportActionBar().hide();
    }
}

