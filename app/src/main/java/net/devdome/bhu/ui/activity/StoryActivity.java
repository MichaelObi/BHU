package net.devdome.bhu.ui.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.devdome.bhu.R;

public class StoryActivity extends BaseActivity implements View.OnClickListener {

    ImageView featuredImage;
    Toolbar toolbar;
    ActionBar actionBar;
    //    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton fabComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        featuredImage = (ImageView) findViewById(R.id.backdrop);
//        Picasso.with(this).load("http://wallpoper.com/images/00/45/05/47/green-background-2_00450547.jpg")
//                .centerCrop()
//                .fit()
//                .into(featuredImage);
//        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        fabComment = (FloatingActionButton) findViewById(R.id.fab_comment);
        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Comments icon clicked", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) featuredImage.getDrawable();
            Palette p = Palette.from(bitmapDrawable.getBitmap()).generate();
            Palette.Swatch swatch = p.getDarkVibrantSwatch();
            if (swatch != null) {
                toolbar.setTitleTextColor((swatch.getTitleTextColor()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(swatch.getRgb());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {

    }
}
