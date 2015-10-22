package net.devdome.bhu.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.db.NewsDatabase;
import net.devdome.bhu.model.Post;
import net.devdome.bhu.ui.activity.StoryActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    List<Post> posts;
    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
        NewsDatabase db = new NewsDatabase(this.context);
        this.posts = Post.getPostList(db.getRecent());
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_news, viewGroup, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder viewHolder, int i) {
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, StoryActivity.class);
                context.startActivity(intent);
            }
        });
        viewHolder.postTitle.setText(posts.get(i).getPostTitle());
        viewHolder.postExcerpt.setText(posts.get(i).getPostContent());
        viewHolder.postAuthor.setText(posts.get(i).getAuthorName());
        viewHolder.postTime.setText(DateUtils.getRelativeTimeSpanString(context, posts.get(i).getUpdatedAt()));
        if (posts.get(i).getFeaturedImageLink() != null && posts.get(i).getFeaturedImageLink().length() > 0) {
            viewHolder.featuredImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(posts.get(i).getFeaturedImageLink())
                    .placeholder(R.drawable.bhu)
                    .into(viewHolder.featuredImage);
        }
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
    }
}
