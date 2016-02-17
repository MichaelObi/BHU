package net.devdome.bhu.app.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.rest.RestClient;
import net.devdome.bhu.app.rest.model.Avatar;
import net.devdome.bhu.app.rest.service.ApiService;

import java.io.File;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static net.devdome.bhu.app.Config.REQUEST_CODE_ASK_PERMISSION;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends BaseActivity {

    TextView tvProfileTitle, tvEmailAddress, tvLevel, tvDepartment, tvMatricNo;
    ImageView imgProfile;
    LinearLayout topCardContent;
    Toolbar toolbar;

    SharedPreferences prefs;
    ProgressBar progressBar;

    public static String getMimeTypeOfFile(String pathName) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opt);
        return opt.outMimeType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prefs = getSharedPreferences(Config.KEY_USER_PROFILE, Context.MODE_PRIVATE);
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        topCardContent = (LinearLayout) findViewById(R.id.profile_top_card_content);
        tvProfileTitle = (TextView) findViewById(R.id.profile_title);
        tvDepartment = (TextView) findViewById(R.id.tv_department);
        tvEmailAddress = (TextView) findViewById(R.id.tv_email_address);
        tvLevel = (TextView) findViewById(R.id.tv_level);
        tvMatricNo = (TextView) findViewById(R.id.tv_matric_no);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        tvDepartment.setText(String.format("%s department", prefs.getString(Config.KEY_DEPARTMENT_NAME, "Unknown")));
        tvEmailAddress.setText(prefs.getString(Config.KEY_EMAIL, "Unknown"));
        tvLevel.setText(String.format("%s level", prefs.getString(Config.KEY_LEVEL, "Unknown")));
        tvMatricNo.setText(prefs.getString(Config.KEY_MATRIC_NO, "Unknown"));
        tvProfileTitle.setText(String.format("%s %s", prefs.getString(Config.KEY_FIRST_NAME, ""), prefs.getString(Config.KEY_LAST_NAME, "")));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.profile));
        setSupportActionBar(toolbar);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String imgUrl = getAvatarUrl();
        Log.i(Config.TAG, "Avatar URL: " + imgUrl);
        loadAvatar(imgUrl);

    }

    @NonNull
    private String getAvatarUrl() {
        String imgUrl = prefs.getString(Config.KEY_AVATAR, Config.DEFAULT_AVATAR_URL);
        if (!imgUrl.equals(Config.DEFAULT_AVATAR_URL)) {
            if (!imgUrl.startsWith(Config.HOME_URL)) {
                imgUrl = Config.HOME_URL.concat(imgUrl);
            }
        }
        return imgUrl;
    }

    private void loadAvatar(final String imgUrl) {
        Picasso picasso = new Picasso.Builder(this).build();
        picasso.setIndicatorsEnabled(true);
        progressBar.setVisibility(View.VISIBLE);
        picasso.load(imgUrl)
                .error(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_person_24dp))
                .placeholder(R.drawable.ic_person_24dp)
                .into(imgProfile, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (!imgUrl.equals(Config.DEFAULT_AVATAR_URL)) {
                            Palette palette = new Palette.Builder(((BitmapDrawable) imgProfile.getDrawable()).getBitmap())
                                    .generate();
                            Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();

                            if (darkMutedSwatch != null) {
                                topCardContent.setBackgroundColor(darkMutedSwatch.getRgb());
                                toolbar.setBackgroundColor(darkMutedSwatch.getRgb());
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(ProfileActivity.this, "Failed to load avatar", Toast.LENGTH_SHORT).show();
                    }
                });
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setEnabled(false);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_upload_avatar:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSION);
                    break;
                }
                Crop.pickImage(this);
                break;
            default:
        }
        item.setEnabled(true);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Crop.pickImage(this);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    Uri image = data.getData();
                    File file = new File(getRealPathFromURI(image));
                    String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getMimeTypeOfFile(file.getPath()));

                    if (extension == null) {
                        //Extension Null break
                        break;
                    }

                    String fileName = System.currentTimeMillis() + "." + extension;
                    Uri destination = Uri.fromFile(new File(getCacheDir(), fileName));
                    Crop.of(image, destination).asSquare().start(this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    progressBar.setVisibility(View.VISIBLE);

                    Uri selectedImage = Crop.getOutput(data);

                    String path = getRealPathFromURI(selectedImage);
                    Log.e(Config.TAG, "Real PATH: " + path);

                    TypedFile typedImage = new TypedFile("multipart/form-data", new File(path));

                    // Requires longer read timeout
                    ApiService service = new RestClient(60, 30).getApiService();
                    service.uploadAvatar(getAuthToken(), typedImage, new retrofit.Callback<Avatar>() {
                        @Override
                        public void success(Avatar avatar, Response response) {
                            Toast.makeText(ProfileActivity.this, "Image upload was successful", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editPrefs = prefs.edit();
                            editPrefs.putString(Config.KEY_AVATAR, avatar.getAvatar_url());
                            editPrefs.apply();
                            Log.e(Config.TAG, " URL: " + getAvatarUrl());
                            Picasso.with(ProfileActivity.this).invalidate(getAvatarUrl());
                            loadAvatar(getAvatarUrl());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(ProfileActivity.this, "Image upload was not successful", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
                    progressBar.setVisibility(View.GONE);

                }
                break;
            default:
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}

