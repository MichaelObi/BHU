package net.devdome.bhu.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.model.Comment;
import net.devdome.bhu.app.ui.fragment.StoryCommentFragment;

/**
 * Created by Michael on 7/30/2016.
 */

public class CommentsAdapter extends FirebaseRecyclerAdapter<Comment, CommentsAdapter.ViewHolder> {

    public CommentsAdapter(Class modelClass, int modelLayout, Class viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public CommentsAdapter(Class modelClass, int modelLayout, Class viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Comment comment, int position) {
        viewHolder.name.setText(comment.getUser().getName());
        viewHolder.comment.setText(comment.getComment());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, comment;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            comment = (TextView) itemView.findViewById(R.id.tv_comment);
        }
    }
}
