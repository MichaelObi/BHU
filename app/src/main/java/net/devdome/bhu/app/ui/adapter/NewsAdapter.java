package net.devdome.bhu.app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.NewsDatabase;
import net.devdome.bhu.app.model.Post;
import net.devdome.bhu.app.ui.activity.StoryActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    List<Post> posts;
    private Activity activity;

    public NewsAdapter(Activity activity) {
        this.activity = activity;
        NewsDatabase db = new NewsDatabase(this.activity);
        this.posts = Post.getPostList(db.getRecent());
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(activity).inflate(R.layout.card_news, viewGroup, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder viewHolder, int i) {

        viewHolder.setId(posts.get(i).getId());
        viewHolder.postTitle.setText(posts.get(i).getPostTitle());
        viewHolder.postExcerpt.setText(posts.get(i).getPostContent());
        viewHolder.postAuthor.setText(posts.get(i).getAuthorName());
        viewHolder.postTime.setText(DateUtils.getRelativeTimeSpanString(activity, posts.get(i).getUpdatedAt()));
        if (posts.get(i).getFeaturedImageLink() != null && posts.get(i).getFeaturedImageLink().length() > 0) {
            viewHolder.featuredImage.setVisibility(View.VISIBLE);
            Picasso.with(activity).load(posts.get(i).getFeaturedImageLink())
                    .centerInside().fit()
                    .into(viewHolder.featuredImage);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, StoryActivity.class);
                intent.putExtra("article_id", viewHolder.getId());
//                Pair<View, String> pairFeaturedImage = Pair.create((View) viewHolder.featuredImage, "featured_image");
//                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairFeaturedImage);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView postExcerpt, postTitle, postAuthor, postTime;
        ImageView featuredImage;
        View mView;
        private long viewId;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            cardView = (CardView) itemView.findViewById(R.id.card_news);
            postExcerpt = (TextView) itemView.findViewById(R.id.post_excerpt);
            postTitle = (TextView) itemView.findViewById(R.id.post_title);
            featuredImage = (ImageView) itemView.findViewById(R.id.featured_image);
            postAuthor = (TextView) itemView.findViewById(R.id.post_author);
            postTime = (TextView) itemView.findViewById(R.id.post_time);
        }

        public long getId() {
            return this.viewId;
        }

        public void setId(long id) {
            this.viewId = id;
        }
    }
}
