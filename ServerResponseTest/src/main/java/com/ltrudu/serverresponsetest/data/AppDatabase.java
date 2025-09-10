package com.ltrudu.serverresponsetest.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

@Database(entities = {Server.class, Settings.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract ServerDao serverDao();
    public abstract SettingsDao settingsDao();
    
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create settings table
            database.execSQL("CREATE TABLE IF NOT EXISTS `settings` ("
                    + "`id` INTEGER NOT NULL, "
                    + "`time_between_requests` INTEGER NOT NULL, "
                    + "`request_delay_ms` INTEGER NOT NULL, "
                    + "`random_min_delay_ms` INTEGER NOT NULL, "
                    + "`random_max_delay_ms` INTEGER NOT NULL, "
                    + "`infinite_requests` INTEGER NOT NULL, "
                    + "`number_of_requests` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`id`))");
            
            // Insert default settings
            database.execSQL("INSERT INTO settings (id, time_between_requests, request_delay_ms, "
                    + "random_min_delay_ms, random_max_delay_ms, infinite_requests, number_of_requests) "
                    + "VALUES (1, 5, 100, 50, 100, 1, 10)");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "server_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}