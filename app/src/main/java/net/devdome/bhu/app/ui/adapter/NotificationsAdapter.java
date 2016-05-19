package net.devdome.bhu.app.ui.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Notification;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by Michael on 4/24/2016.
 */
public class NotificationsAdapter extends RealmBasedRecyclerViewAdapter<Notification, NotificationsAdapter.ViewHolder> {

    private Context context;

    public NotificationsAdapter(Context context, RealmResults<Notification> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.list_item_notification, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int i) {
        final Notification notification = realmResults.get(i);
        viewHolder.title.setText(notification.getSubject());
        viewHolder.dateTime.setText(DateUtils.getRelativeTimeSpanString(context, notification.getCreated_at()));
    }

    public class ViewHolder extends RealmViewHolder {

        TextView title, dateTime;
        View v;

        public ViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            this.title = (TextView) itemView.findViewById(R.id.notification_title);
            this.dateTime = (TextView) itemView.findViewById(R.id.notification_date);

        }
    }
}
