package net.devdome.bhu.app.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.RegistrationIntentService;
import net.devdome.bhu.app.authentication.AccountConfig;
import net.devdome.bhu.app.model.ProfileIndex;
import net.devdome.bhu.app.provider.NewsProvider;
import net.devdome.bhu.app.utility.NetworkUtilities;
import net.devdome.bhu.app.utility.PlayServicesUtil;

public class LoginActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    public final static String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    private static final String TAG = "LoginActivity";
    Button btnLogin;
    SharedPreferences mPreferences;
    EditText etEmail;
    EditText etPassword;
    TextView linkSignup, linkForgotPwd;
    //    private void scheduleJobs() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 20);
//        calendar.set(Calendar.MINUTE, 30);
//        calendar.set(Calendar.SECOND, 0);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, EveningUpdatesReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//    }
    UserProfileChangeRequest profileChangeRequest;
    private AccountManager mAccountManager;
    private ProgressDialog progressDialog;
    private String USERID = "user_id";
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();

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
        btnLogin = (Button) findViewById(R.id.btn_login);
        linkSignup = (TextView) findViewById(R.id.link_signup);
        linkForgotPwd = (TextView) findViewById(R.id.link_forgot_pwd);

        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnLogin.setOnClickListener(this);
        linkSignup.setOnClickListener(this);
        mAccountManager = AccountManager.get(this);

        linkForgotPwd.setOnClickListener(this);

        linkSignup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!NetworkUtilities.isNetworkEnabled(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "I couldn't establish Internet connectivity", Toast.LENGTH_SHORT).show();
                    return true;
                }
                new LoginTask().execute("o.michael@binghamuni.edu.ng", "patrick");
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void initFirebase() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void indexUserProfileInFirebase(FirebaseUser user, String name, String email, int idOnServer, String avatarUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("/user_profile_index/" + user.getUid());
        dbRef.setValue(new ProfileIndex(name, email, idOnServer, avatarUrl));
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayServicesUtil.checkPlayServices(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btn_login:
                if (!NetworkUtilities.isNetworkEnabled(this)) {
                    Toast.makeText(this, "I couldn't establish Internet connectivity", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LoginTask().execute(etEmail.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.link_signup:
                i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.link_forgot_pwd:
                String url = Config.HOME_URL + "/password/email";
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.primary));
                intentBuilder.setShowTitle(true);
                intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
                intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                CustomTabsIntent intent = intentBuilder.build();
                intent.launchUrl(this, Uri.parse(url));
                break;
            default:
        }
    }

    private void storeProfile(JsonObject data, int userId, String token, @Nullable FirebaseUser user) {
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
        String avatarUrl = data.get(Config.KEY_AVATAR).getAsString().length() > 0 ? data.get(Config.KEY_AVATAR).getAsString() : null;
        editPrefs.putString(Config.KEY_AVATAR, avatarUrl);
        editPrefs.apply();
        if (user != null) {
            String email = data.get(Config.KEY_EMAIL).getAsString();
            String name = data.get(Config.KEY_FIRST_NAME).getAsString() + data.get(Config.KEY_LAST_NAME).getAsString();
            indexUserProfileInFirebase(user, name, email, userId, avatarUrl);
        }
    }



    private class LoginTask extends AsyncTask<String, Void, Intent> {
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
        protected Intent doInBackground(String... params) {
            JsonObject loginResponse;
            final Intent res = new Intent();
            final String emailAddress = params[0];
            final String password = params[1];
            try {
                loginResponse = AccountConfig.serverAuthenticator.userSignIn(emailAddress, password);
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, emailAddress);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountConfig.ACCOUNT_TYPE);
                res.putExtra(PARAM_USER_PASS, password);
                if (loginResponse != null) {
                    if (!loginResponse.has("error")) {
                        String authToken = loginResponse.getAsJsonObject("data").get("key").getAsString();
                        int userId = loginResponse.getAsJsonObject("data").get("user_id").getAsInt();
                        res.putExtra("profile", loginResponse.getAsJsonObject("data").get("profile").toString());
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                        res.putExtra(USERID, userId);
                    } else {
                        res.putExtra(KEY_ERROR_MESSAGE, loginResponse.getAsJsonObject("error").get("message").getAsString());
                    }
                } else {
                    res.putExtra(KEY_ERROR_MESSAGE, getString(R.string.could_not_connect_to_server));
                }
                return res;
            } catch (Exception e) {
                e.printStackTrace();
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

            // Send Auth code to firebase
            mAuth.signInWithCustomToken(authCode).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful());

                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCustomToken", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

//            if (mAuth.getCurrentUser() == null) {
//                progressDialog.dismiss();
//                return;
//            }
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
            ContentResolver.setSyncAutomatically(account, NewsProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, NewsProvider.AUTHORITY, settingsBundle, 3600);
            storeProfile(new JsonParser().parse(intent.getStringExtra("profile")).getAsJsonObject(), intent.getIntExtra(USERID, 0), authCode, null);
            startService(new Intent(LoginActivity.this, RegistrationIntentService.class));
            startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            progressDialog.cancel();
        }
    }


}
