package net.devdome.bhu.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.signin.GoogleSignInAccount;

import net.devdome.bhu.Config;
import net.devdome.bhu.utility.PlayServicesUtil;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

public class BaseActivity extends AppCompatActivity{

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private Boolean resolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mPreferences = getSharedPreferences(Config.KEY_USER_PROFILE, MODE_PRIVATE);
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
}
