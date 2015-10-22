package net.devdome.bhu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class NewsDatabase extends DatabaseHelper {


    public static final String TABLE_NAME = "posts";
    public static final String KEY_POST_ID = "post_id";
    public static final String KEY_POST_TITLE = "post_title";
    public static final String KEY_POST_CONTENT = "post_content";
    public static final String KEY_POST_CONTENT_HTML = "post_content_html";
    public static final String KEY_POST_FEATURED_IMAGE = "post_featured_image";
    public static final String KEY_POST_AUTHOR_ID = "author_id";
    public static final String KEY_POST_AUTHOR_NAME = "author_name";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_POST_ID + " INTEGER NOT NULL,"
            + KEY_POST_TITLE + " TEXT NOT NULL,"
            + KEY_POST_CONTENT + " TEXT NOT NULL,"
            + KEY_POST_CONTENT_HTML + " TEXT NOT NULL,"
            + KEY_POST_FEATURED_IMAGE + " TEXT,"
            + KEY_POST_AUTHOR_NAME + " TEXT NOT NULL,"
            + KEY_POST_AUTHOR_ID + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " INTEGER NOT NULL,"
            + KEY_UPDATED_AT + " INTEGER NOT NULL,"
            + "UNIQUE (" + KEY_POST_ID + ") ON CONFLICT REPLACE)";
    public static String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ng.edu.binghamuni.bhu.provider.posts";
    public static String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.ng.edu.binghamuni.bhu.provider.posts";
    String[] KEYS = new String[]{
            BaseColumns._ID,
            KEY_POST_ID,
            KEY_POST_TITLE,
            KEY_POST_CONTENT,
            KEY_POST_CONTENT_HTML,
            KEY_POST_FEATURED_IMAGE,
            KEY_POST_AUTHOR_NAME,
            KEY_POST_AUTHOR_ID,
            KEY_CREATED_AT,
            KEY_UPDATED_AT
    };
    SQLiteDatabase db;

    public NewsDatabase(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getRecent() {
        db = getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, KEYS, null, null, null, null, KEY_UPDATED_AT + " DESC");
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }


    public Cursor find(int id) {
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM news WHERE " + "_id" + " = '" + id + "'", null);
        c.moveToFirst();
        return c;
    }
    /**
     * @param values
     * @return
     */
    public long insert(ContentValues values) {
        try {
            db = getWritableDatabase();
            long id = db.insert(TABLE_NAME, null, values);
            db.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
