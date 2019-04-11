package com.example.micandmaster.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {AudioEntity.class}, version = 1)
public abstract class AudioDatabase extends RoomDatabase {
    public abstract AudioDao audioDao();
    private static volatile AudioDatabase INSTANCE;

    // Class is a singleton
    public static AudioDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AudioDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AudioDatabase.class, "audio_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
