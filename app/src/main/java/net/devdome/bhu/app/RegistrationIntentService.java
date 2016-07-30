package net.devdome.bhu.app;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.rest.RestClient;
import net.devdome.bhu.app.rest.model.Blank;
import net.devdome.bhu.app.rest.service.ApiService;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in a service on a
 * separate handler thread. <p> TODO: Customize class - update intent actions and extra parameters.
 */
public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // [Start get token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [End get token]

            Log.i(Config.TAG, "GCM Registration Token: " + token);

            //send token to server
            sendRegistrationTokenToServer(token);

            sharedPreferences.edit().putBoolean(Config.GCM_SENT_TOKEN_TO_SERVER, true).apply();
        } catch (IOException | RetrofitError e) {
            e.printStackTrace();
            sharedPreferences.edit().putBoolean(Config.GCM_SENT_TOKEN_TO_SERVER, false).apply();
        }

    }

    private void sendRegistrationTokenToServer(String token) throws RetrofitError {
        ApiService apiService = new RestClient().getApiService();
        String authCode = (this.getSharedPreferences(Config.KEY_USER_PROFILE, MODE_PRIVATE)).getString(Config.KEY_AUTH_TOKEN, null);
        if (authCode != null) {
            Log.i(Config.TAG, "Sending token to server");
            apiService.sendGcmTokenToServer(authCode, token, new Callback<Blank>() {
                @Override
                public void success(Blank blank, Response response) {
                    Log.i(Config.TAG, "Device token sent to server");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i(Config.TAG, "Device token not sent to server");
                    throw error;
                }
            });
        }
    }

}
