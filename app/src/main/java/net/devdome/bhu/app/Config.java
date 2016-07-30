package net.devdome.bhu.app;

import com.google.android.gms.maps.model.LatLng;

public class Config {


    public static final String HOME_URL = "http://bhu.devdome.net";
    public static final String BASE_URL = "http://bhu.devdome.net/api/v2";
//    public static final String BASE_URL = "http://192.168.1.100/api/v2";

    public static final String TAG = "net.devdome.bhu.app";

    // Google Cloud Messaging constants
    public static final String GCM_SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";


    public static final String DEFAULT_AVATAR_URL = "http://kudago.com/static/img/default-avatar.png";
    public final static String KEY_USER_PROFILE = "user_profile";
    public final static String KEY_USER_ID = "user_id";
    public final static String KEY_MATRIC_NO = "matric_no";
    public final static String KEY_EMAIL = "email";
    public final static String KEY_FIRST_NAME = "first_name";
    public final static String KEY_LAST_NAME = "last_name";
    public final static String KEY_LEVEL = "level";
    public final static String KEY_DEPARTMENT_NAME = "department_name";
    public final static String KEY_DEPARTMENT_CODE = "department_code";
    public final static String KEY_AVATAR = "avatar";
    public final static String KEY_DEPARTMENT = "department";
    public final static String KEY_CODE = "code";
    public final static String KEY_NAME = "name";
    public final static String KEY_AUTH_TOKEN = "auth_token";
    public final static String FIRST_LAUNCH = "first_launch";
    // Sync complete broadcast message key
    public final static String EXTRA_POSTS_ADDED = "new_posts_added";

    // Period in seconds between syncs
    public final static long SYNC_PERIOD = 7200; //2 hours
    public static long REALM_SCHEMA_VERSION = 3;

    // HTTP Client setup
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 15;
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 30;
    public static final int REQUEST_CODE_ASK_PERMISSION = 101;


    //Map data
    public static final double LATITUDE = 8.957914;
    public static final double LONGITUDE = 7.699131;
    public static final LatLng gpsLocation = new LatLng(LATITUDE, LONGITUDE);
}