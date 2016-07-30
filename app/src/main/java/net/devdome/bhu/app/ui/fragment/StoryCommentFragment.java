package net.devdome.bhu.app.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.model.Comment;
import net.devdome.bhu.app.model.ProfileIndex;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

public class StoryCommentFragment extends BottomSheetDialogFragment {
    public static final String ARTICLE_ID = "article_id";
    FloatingActionButton fabSendComment;
    View contentView;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProfileIndex user;
    RecyclerView rvComments;
    FirebaseRecyclerAdapter adapter;
    Context context;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior
            .BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
    private BottomSheetStateCallback bottomSheetStateCallback;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_story_comments, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        rvComments = (RecyclerView) contentView.findViewById(R.id.rv_comments);
        rvComments.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(true);
        rvComments.setLayoutManager(layoutManager);

        final EditText tvComment = (EditText) contentView.findViewById(R.id.tv_comment);
        fabSendComment = (FloatingActionButton) contentView.findViewById(R.id.sendButton);
        fabSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(tvComment.getText().toString());
                tvComment.setText("");
            }
        });

        Toolbar toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);

            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);

        }

        setupCommentsRecyclerView();

        fabSendComment.show();
    }

    private void setupCommentsRecyclerView() {
        int articleId = getArguments().getInt(ARTICLE_ID);
        if (articleId == 0) {
            Toast.makeText(getActivity(), "Comments not available.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(String.format(Locale.getDefault(), "/news/%d/comments", articleId));
        reference.orderByChild("createdAt");
        adapter = new FirebaseRecyclerAdapter<Comment,
                CommentsViewHolder>(
                Comment.class,
                R.layout.card_comment,
                CommentsViewHolder.class,
                reference) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comment comment, int position) {
                viewHolder.setName(comment.getUser().getName());
                viewHolder.setComment(comment.getComment());
                viewHolder.loadProfileImage(comment.getUser().getAvatarUrl());
                viewHolder.setTime(comment.createdAt);
            }
        };


        rvComments.setAdapter(adapter);

        SharedPreferences prefs = getActivity().getSharedPreferences(Config.KEY_USER_PROFILE, Context.MODE_PRIVATE);
        String imgUrl = prefs.getString(Config.KEY_AVATAR, Config.DEFAULT_AVATAR_URL);
        user = new ProfileIndex(
                String.format("%s %s", prefs.getString(Config.KEY_FIRST_NAME, ""), prefs.getString(Config
                        .KEY_LAST_NAME, "")),
                prefs.getString(Config.KEY_EMAIL, "Unknown"),
                prefs.getInt(Config.KEY_USER_ID, 0),
                getAvatarUrl(imgUrl)
        );
    }

    private void sendComment(String strComment) {
        if (strComment.isEmpty()) return;

        Comment comment = new Comment(strComment, user);

        reference.push().setValue(comment);
    }

    private String getAvatarUrl(String imgUrl) {
        if (!imgUrl.equals(Config.DEFAULT_AVATAR_URL)) {
            if (!imgUrl.startsWith(Config.HOME_URL)) {
                imgUrl = Config.HOME_URL.concat(imgUrl);
            }
        }
        return imgUrl;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        fabSendComment.hide();
        if (bottomSheetStateCallback != null) {
            bottomSheetStateCallback.onDismiss();
        }
    }

    public void setBottomSheetStateCallback(BottomSheetStateCallback callback) {
        bottomSheetStateCallback = callback;
    }

    /**
     * Gives room for context to act before or after lifecycle changes if implemented
     */
    public interface BottomSheetStateCallback {
        /**
         * Called to notify context about a dismissing dialog
         */
        void onDismiss();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View view;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            ((TextView) itemView.findViewById(R.id.tv_name)).setText(name);
        }

        public void setComment(String comment) {
            ((TextView) itemView.findViewById(R.id.tv_comment)).setText(comment);
        }

        public void loadProfileImage(String url) {
            ImageView profilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
            Picasso.with(view.getContext())
                    .load(url)
                    .config(Bitmap.Config.RGB_565)
                    .into(profilePhoto);
        }

        public void setTime(long duration) {
            PrettyTime prettyTime = new PrettyTime();
            String time = prettyTime.format(new Date(duration));
            ((TextView) view.findViewById(R.id.tv_time)).setText(time);
        }
    }
}
