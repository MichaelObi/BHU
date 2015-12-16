package net.devdome.bhu.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.provider.NewsProvider;
import net.devdome.bhu.ui.fragment.NewsFragment;
import net.devdome.bhu.ui.fragment.ProfileFragment;

import java.util.Objects;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView profileImage;
    TextView tvNameNav, tvEmailNav, tvDeptNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Slide exitSlide = new Slide();
        exitSlide.setSlideEdge(Gravity.END);
        getWindow().setExitTransition(exitSlide);

        Slide enterSlide = new Slide();
        enterSlide.setSlideEdge(Gravity.END);
        getWindow().setReenterTransition(enterSlide);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(this);
        navigate(R.id.nav_news);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        profileImage = (ImageView) drawerView.findViewById(R.id.nav_drawer_profile_img);
        tvNameNav = (TextView) findViewById(R.id.nav_drawer_name);
        tvEmailNav = (TextView) findViewById(R.id.nav_drawer_email);
        tvDeptNav = (TextView) findViewById(R.id.nav_drawer_department);

        String imgUrl = mPreferences.getString(Config.KEY_AVATAR, "http://kudago.com/static/img/default-avatar.png");
        tvNameNav.setText(String.format("%s %s", mPreferences.getString(Config.KEY_FIRST_NAME, ""), mPreferences.getString(Config.KEY_LAST_NAME, "")));
        tvDeptNav.setText(mPreferences.getString(Config.KEY_DEPARTMENT_NAME, ""));
        tvEmailNav.setText(mPreferences.getString(Config.KEY_EMAIL, ""));
        Picasso.with(this)
                .load(imgUrl)
                .into(profileImage);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }


    /**
     * Navigate to the fragment with the Drawer menu Id specified
     *
     * @param itemId id of the {@link MenuItem} clicked
     */
    private void navigate(int itemId) {

        String title = "";
        Fragment fragment;
        switch (itemId) {
            case R.id.nav_news:
                title = getString(R.string.news);
                fragment = NewsFragment.getInstance();
                loadFragment(fragment, R.id.container, false);
                break;
            case R.id.nav_profile:
                title = getString(R.string.profile);
                fragment = ProfileFragment.getInstance();
                loadFragment(fragment, R.id.container, false);
                break;
            case R.id.nav_my_courses:
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                Intent intent = new Intent(this, MyCoursesActivity.class);
                startActivity(intent, options.toBundle());
                break;
            case R.id.nav_logout:
                AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);

                // loop through get accounts to remove them
                Account[] accounts = accountManager.getAccounts();
                for (Account account : accounts) {
                    if (Objects.equals(account.type.intern(), NewsProvider.AUTHORITY))
                        accountManager.removeAccount(account, this, null, null);
                }

                SharedPreferences prefs = this.getSharedPreferences(Config.KEY_USER_PROFILE, MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            default:
                navigate(R.id.nav_news);
        }
        MenuItem menuItem = navigationView.getMenu().findItem(itemId);
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(title);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }
        navigate(menuItem.getItemId());
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
