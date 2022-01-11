package com.example.dirttrails.SQLite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TileEntity.class, GPXEntity.class},
        version = 2)
//Extends Room DB class
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract TileDao tileDao();
    public abstract GPXDao gpxDao();

    private static volatile AppRoomDatabase appRoomInstance;

    public static AppRoomDatabase getDatabase(final Context context) {
        if (appRoomInstance == null) {
            synchronized (AppRoomDatabase.class) {
                if (appRoomInstance == null) {
                    appRoomInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return appRoomInstance;
    }
}
