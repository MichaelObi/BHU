package net.devdome.bhu.app;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class BHUApplication extends Application {
//    private RefWatcher refWatcher;

//    public static RefWatcher getRefWatcher(Context context) {
//        BHUApplication application = (BHUApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
//        refWatcher = LeakCanary.install(this);
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .schemaVersion(Config.REALM_SCHEMA_VERSION)
                .migration(new BHUMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }


    public class BHUMigration implements RealmMigration {

        @Override
        public void migrate(DynamicRealm dynamicRealm, long oldVersion, long newVersion) {
            RealmSchema schema = dynamicRealm.getSchema();
            if (oldVersion == 0) {
                schema.create("Course")
                        .addField("code", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("title", String.class)
                        .addField("registered", boolean.class);
                oldVersion++;
            }

            if (oldVersion == 1) {
                schema.create("CurricularEvent")
                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("starts_at", long.class)
                        .addField("duration", long.class)
                        .addField("day", String.class)
                        .addField("type", String.class)
                        .addField("venue", String.class)
                        .addRealmObjectField("course", schema.get("Course"));
                oldVersion++;
            }

            if (oldVersion == 2) {
                schema.get("CurricularEvent")
                        .removeField("starts_at")
                        .addField("starts_at", String.class);
                oldVersion++;
            }
        }
    }
}
