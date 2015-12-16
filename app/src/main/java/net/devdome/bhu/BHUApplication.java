package net.devdome.bhu;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class BHUApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
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
        }
    }
}
