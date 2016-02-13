package net.devdome.bhu.app.ui.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.NewsDatabase;
import net.devdome.bhu.app.model.Post;

public class StoryActivity extends BaseActivity implements View.OnClickListener {

    ImageView featuredImage;
    Toolbar toolbar;
    ActionBar actionBar;
    FloatingActionButton fabComment;
    Context context;
    TextView tvTitle, tvBody, tvAuthor, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        context = getApplicationContext();
        NewsDatabase db = new NewsDatabase(context);
        Cursor c = db.find((int) getIntent().getLongExtra("article_id", 0));
        if (c == null) {
            Toast.makeText(this, "The story does not exist.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        Post post = Post.getPost(c);
        featuredImage = (ImageView) findViewById(R.id.backdrop);
        tvTitle = (TextView) findViewById(R.id.story_title);
        WebView webView = (WebView) findViewById(R.id.webview_story);
        tvAuthor = (TextView) findViewById(R.id.story_author);
        tvDate = (TextView) findViewById(R.id.story_date);
        tvTitle.setText(post.getPostTitle());
        webView.loadData(post.getPostContentHtml(), "text/html", "utf-8");
        tvAuthor.setText(post.getAuthorName());
        tvDate.setText(DateUtils.getRelativeTimeSpanString(context, post.getUpdatedAt()));
        if (post.getFeaturedImageLink() != null) {

            if (!post.getFeaturedImageLink().isEmpty()) {
                featuredImage.setVisibility(View.VISIBLE);
                Picasso.with(this).load(post.getFeaturedImageLink())
                        .centerInside().fit()
                        .into(featuredImage);
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) featuredImage.getDrawable();
//            Palette p = Palette.from(bitmapDrawable.getBitmap()).generate();
//            Palette.Swatch swatch = p.getDarkVibrantSwatch();
//            if (swatch != null) {
//                toolbar.setTitleTextColor((swatch.getTitleTextColor()));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    getWindow().setStatusBarColor(swatch.getRgb());
//                }
//            }
//        }
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
