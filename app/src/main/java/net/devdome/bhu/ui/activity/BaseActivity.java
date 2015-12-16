package net.devdome.bhu.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import net.devdome.bhu.Config;
import net.devdome.bhu.utility.PlayServicesUtil;

public class BaseActivity extends AppCompatActivity {

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    SharedPreferences mPreferences;
    private Boolean resolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(Config.KEY_USER_PROFILE, MODE_PRIVATE);
        if (mPreferences.getInt(Config.KEY_USER_ID, 0) == 0) {
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayServicesUtil.checkPlayServices(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void loadFragment(Fragment fragment, int containerId, Boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().replace(containerId, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getTag());
        }
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public String getAuthToken() {
        return mPreferences.getString(Config.KEY_AUTH_TOKEN, null);
    }
}
