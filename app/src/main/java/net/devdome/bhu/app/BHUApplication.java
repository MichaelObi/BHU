package net.devdome.bhu.app;

import android.app.Application;

import pl.tajchert.nammu.Nammu;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class BHUApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "VY9H2WYirUpn7V7wZiev0guRE";
    private static final String TWITTER_SECRET = "0XIVwbAaRw4atro81dxGEjpAZjGrTpDvpiUhkecdwQKFXH9Hpx";

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
        Nammu.init(this); // For permissions
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
            if (oldVersion == 3) {
                schema.create("Notification")
                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("subject", String.class)
                        .addField("message", String.class)
                        .addField("created_at", long.class);
                oldVersion++;
            }

            if (oldVersion == 4) {
                schema.get("Notification")
                        .addField("read", Boolean.class);
                oldVersion++;
            }
        }
    }
}
