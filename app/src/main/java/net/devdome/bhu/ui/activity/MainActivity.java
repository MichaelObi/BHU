package net.devdome.bhu.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.ui.fragment.NewsFragment;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        String imgUrl = mPreferences.getString(Config.KEY_AVATAR, "http://kudago.com/static/img/default-avatar.png");
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
                loadFragment(fragment);
                break;
            case R.id.nav_logout:
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
        if (!menuItem.isChecked()) {
            if (menuItem.isCheckable()) {
                menuItem.setChecked(true);
            }
            navigate(menuItem.getItemId());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        fragmentManager.executePendingTransactions();
    }
}
