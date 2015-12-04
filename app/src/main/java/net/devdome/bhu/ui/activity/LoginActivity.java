package net.devdome.bhu.ui.activity;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.authentication.AccountConfig;
import net.devdome.bhu.provider.NewsProvider;
import net.devdome.bhu.utility.NetworkUtilities;

import java.io.IOException;

public class LoginActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    public final static String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    AppCompatButton btnLogin;
    SharedPreferences mPreferences;
    EditText etEmail;
    EditText etPassword;
    GoogleCloudMessaging gcm;
    TextView linkSignup;
    private AccountManager mAccountManager;
    private ProgressDialog progressDialog;
    private String USERID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(Config.KEY_USER_PROFILE, MODE_PRIVATE);
        if (mPreferences.getInt(Config.KEY_USER_ID, 0) != 0) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_login);
        etEmail = ((EditText) findViewById(R.id.et_emailAddress));
        etPassword = ((EditText) findViewById(R.id.et_password));
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        linkSignup = (TextView) findViewById(R.id.link_signup);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnLogin.setOnClickListener(this);
        linkSignup.setOnClickListener(this);
        mAccountManager = AccountManager.get(getBaseContext());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            if (!NetworkUtilities.isNetworkEnabled(this)) {
                Toast.makeText(this, "I couldn't establish Internet connectivity", Toast.LENGTH_SHORT).show();
                return;
            }
            final String emailAddress = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            new AsyncTask<Void, Void, Intent>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(LoginActivity.this, R.style.MaterialTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Logging you in...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                @Override
                protected Intent doInBackground(Void... params) {
                    JsonObject loginResponse;
                    final Intent res = new Intent();
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                    String token = null;
                    try {
                        token = instanceID.getToken(Config.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (token == null) {
                        res.putExtra(KEY_ERROR_MESSAGE, "A server error occurred. Try again later.");
                        return res;
                    }
                    Log.i(Config.TAG, "GCM token: " + token);
                    try {
                        loginResponse = AccountConfig.serverAuthenticator.userSignIn(emailAddress, password, token);
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, emailAddress);
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountConfig.ACCOUNT_TYPE);
                        res.putExtra(PARAM_USER_PASS, password);
                        if (!loginResponse.has("error")) {
                            String authToken = loginResponse.getAsJsonObject("data").get("key").getAsString();
                            int userId = loginResponse.getAsJsonObject("data").get("user_id").getAsInt();
                            res.putExtra("profile", loginResponse.getAsJsonObject("data").get("profile").toString());
                            res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                            res.putExtra(USERID, userId);
                        } else {
                            res.putExtra(KEY_ERROR_MESSAGE, loginResponse.getAsJsonObject("error").get("message").getAsString());
                        }
                        return res;
                    } catch (Exception e) {
                        Log.e(Config.TAG, e.getMessage());
                        res.putExtra(KEY_ERROR_MESSAGE, "A server error occurred. Try again later.");
                        return res;
                    }
                }

                public void onPostExecute(Intent intent) {
                    if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                        Toast.makeText(LoginActivity.this, intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        return;
                    }
                    String authCode = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

                    String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//                    String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                    String password = intent.getStringExtra(PARAM_USER_PASS);
                    final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                    Log.i(Config.TAG, "Token: " + authCode);
                    mAccountManager.addAccountExplicitly(account, password, null);
                    mAccountManager.setAuthToken(account, AccountConfig.TOKEN_TYPE, authCode);
                    setAccountAuthenticatorResult(intent.getExtras());
                    setResult(RESULT_OK, intent);

                    Bundle settingsBundle = new Bundle();
//                    settingsBundle.putBoolean(
//                            ContentResolver.SYNC_EXTRAS_MANUAL, true);
                    ContentResolver.setIsSyncable(account, NewsProvider.AUTHORITY, 1);
//                    ContentResolver.setSyncAutomatically(account, NewsProvider.AUTHORITY, true);
                    ContentResolver.addPeriodicSync(account, NewsProvider.AUTHORITY, settingsBundle, 3600);
                    storeProfile(new JsonParser().parse(intent.getStringExtra("profile")).getAsJsonObject(), intent.getIntExtra(USERID, 0), authCode);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                    progressDialog.cancel();
                }
            }.execute();
        } else if (v.getId() == linkSignup.getId()) {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }

    private void storeProfile(JsonObject data, int userId, String token) {
        SharedPreferences.Editor editPrefs = this.getSharedPreferences(Config.KEY_USER_PROFILE, Context.MODE_PRIVATE).edit();
        editPrefs.putBoolean(Config.FIRST_LAUNCH, true);
        editPrefs.putString(Config.KEY_AUTH_TOKEN, token);
        editPrefs.putInt(Config.KEY_USER_ID, userId);
        editPrefs.putString(Config.KEY_MATRIC_NO, data.get(Config.KEY_MATRIC_NO).getAsString());
        editPrefs.putString(Config.KEY_EMAIL, data.get(Config.KEY_EMAIL).getAsString());
        editPrefs.putString(Config.KEY_FIRST_NAME, data.get(Config.KEY_FIRST_NAME).getAsString());
        editPrefs.putString(Config.KEY_LAST_NAME, data.get(Config.KEY_LAST_NAME).getAsString());
        editPrefs.putString(Config.KEY_LEVEL, data.get(Config.KEY_LEVEL).getAsString());
        editPrefs.putString(Config.KEY_DEPARTMENT_NAME, data.getAsJsonObject(Config.KEY_DEPARTMENT).get(Config.KEY_NAME).getAsString());
        editPrefs.putString(Config.KEY_DEPARTMENT_CODE, data.getAsJsonObject(Config.KEY_DEPARTMENT).get(Config.KEY_CODE).getAsString());
        editPrefs.putString(Config.KEY_AVATAR, data.get(Config.KEY_AVATAR).getAsString());
        editPrefs.apply();
    }
}
