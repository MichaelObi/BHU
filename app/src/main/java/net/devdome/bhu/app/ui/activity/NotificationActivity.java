package net.devdome.bhu.app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Notification;
import net.devdome.bhu.app.ui.adapter.NotificationsAdapter;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class NotificationActivity extends AppCompatActivity {
    NotificationsAdapter notificationsAdapter;
    Realm realm;
    RealmResults<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for now
        Toast.makeText(NotificationActivity.this, "Feature's in the works. ", Toast.LENGTH_SHORT).show();
        finish();
//        setContentView(R.layout.activity_notification);
//
//        RealmRecyclerView recyclerView = (RealmRecyclerView) findViewById(R.id.rv_notifications);
//        realm = Realm.getDefaultInstance();
//        notifications = realm.where(Notification.class).findAllSorted("created_at", Sort.DESCENDING);
//        notificationsAdapter = new NotificationsAdapter(this, notifications, true, true);
//        recyclerView.setAdapter(notificationsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
