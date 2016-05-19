package net.devdome.bhu.app.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Notification;
import net.devdome.bhu.app.provider.NewsProvider;
import net.devdome.bhu.app.ui.fragment.CampusServicesFragment;
import net.devdome.bhu.app.ui.fragment.HomeFragment;
import net.devdome.bhu.app.ui.fragment.MapsFragment;
import net.devdome.bhu.app.ui.fragment.NewsFragment;
import net.devdome.bhu.app.ui.fragment.SocialsFragment;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    ImageView profileImage;
    TextView tvNameNav, tvEmailNav, tvDeptNav;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Slide exitSlide = new Slide();
//        exitSlide.setSlideEdge(Gravity.END);
//        getWindow().setExitTransition(exitSlide);

//        Slide enterSlide = new Slide();
//        enterSlide.setSlideEdge(Gravity.END);
//        getWindow().setReenterTransition(enterSlide);

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
        scheduleJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Check that unread notifications exist
        Realm realm = Realm.getDefaultInstance();
        List<Notification> unreadNotifications = realm.where(Notification.class).equalTo("read", false).findAll();
        if (unreadNotifications.size() > 0) {
            MenuItem item = menu.findItem(R.id.action_notifications);
            item.setIcon(R.drawable.ic_notifications_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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

        String imgUrl = mPreferences.getString(Config.KEY_AVATAR, Config.DEFAULT_AVATAR_URL) + "?small=1";
        tvNameNav.setText(String.format("%s %s", mPreferences.getString(Config.KEY_FIRST_NAME, ""), mPreferences.getString(Config.KEY_LAST_NAME, "")));
        tvDeptNav.setText(mPreferences.getString(Config.KEY_DEPARTMENT_NAME, ""));
        tvEmailNav.setText(mPreferences.getString(Config.KEY_EMAIL, ""));

        if (!imgUrl.equals(Config.DEFAULT_AVATAR_URL)) {
            if (!imgUrl.startsWith(Config.HOME_URL)) {
                imgUrl = Config.HOME_URL.concat(imgUrl);
            }
        }

        Log.i(Config.TAG, "Avatar URL: " + imgUrl);
        Picasso.with(this)
                .load(imgUrl)
                .error(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_person_24dp))
                .into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Palette palette = new Palette.Builder(((BitmapDrawable) profileImage.getDrawable()).getBitmap())
                                .generate();
                        Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                        if (darkMutedSwatch != null) {
                            RelativeLayout navHeader = (RelativeLayout) findViewById(R.id.nav_header);
                            navHeader.setBackgroundColor(darkMutedSwatch.getRgb());
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
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

        Fragment fragment;
        Intent intent;
        switch (itemId) {
            case R.id.nav_dashboard:
                title = getString(R.string.dashboard);
                fragment = new HomeFragment();
                loadFragment(fragment, R.id.container, false);
                break;
            case R.id.nav_news:
                title = getString(R.string.news);
                fragment = NewsFragment.getInstance();
                loadFragment(fragment, R.id.container, false);
                break;
            case R.id.nav_profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_my_courses:
                intent = new Intent(this, MyCoursesActivity.class);
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.nav_navigation:
                title = getString(R.string.navigation);
                fragment = MapsFragment.getInstance();
                loadFragment(fragment, R.id.container, true);
                break;
            case R.id.nav_services:
                title = getString(R.string.campus_services);
                fragment = new CampusServicesFragment();
                loadFragment(fragment, R.id.container, false);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.nav_social:
                title = getString(R.string.social) + "- #BinghamUni";
                fragment = new SocialsFragment();
                loadFragment(fragment, R.id.container, false);
                break;
            case R.id.nav_logout:
                AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);

                // loop through get accounts to remove them
                Account[] accounts = accountManager.getAccounts();
                for (Account account : accounts) {
                    if (account.type.intern().equals(NewsProvider.AUTHORITY))
                        accountManager.removeAccount(account, this, null, null);

                }

                SharedPreferences prefs = this.getSharedPreferences(Config.KEY_USER_PROFILE, MODE_PRIVATE);
                prefs.edit().clear().apply();

                Realm.deleteRealm(Realm.getDefaultInstance().getConfiguration());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ((ActivityManager) getSystemService(ACTIVITY_SERVICE))
                            .clearApplicationUserData(); // note: it has a return value!
                } else {
                    //TODO: use old hacky way, which can be removed once minSdkVersion goes above 19 in a few years.
                }

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

    private void scheduleJobs() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 39);
        calendar.set(Calendar.SECOND, 0);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, EveningUpdatesReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

    }

//    public class EveningUpdatesReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.e(Config.TAG, "Hello, Broadcast for stuff received.");
//            Toast.makeText(getApplicationContext(), "Hello, Broadcast for stuff received.", Toast.LENGTH_SHORT).show();
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context
//                    .NOTIFICATION_SERVICE);
//            Notification.Builder notificationBuilder = new Notification.Builder(context)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setPriority(Notification.PRIORITY_DEFAULT)
//                    .setCategory(Notification.CATEGORY_MESSAGE)
//                    .setContentTitle("Sample Notification")
//                    .setContentText("This is a normal notification.");
//            mNotificationManager.notify(2, notificationBuilder.build());
//        }
//    }

}
