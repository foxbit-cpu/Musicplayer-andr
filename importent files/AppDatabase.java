package com.example.musicplayerexam.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.musicplayerexam.data.dao.PlaylistDao;
import com.example.musicplayerexam.data.dao.TrackDao;
import com.example.musicplayerexam.data.entities.PlaylistEntity;
import com.example.musicplayerexam.data.entities.PlaylistTrackCrossRef;
import com.example.musicplayerexam.data.entities.TrackEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        TrackEntity.class,
        PlaylistEntity.class,
        PlaylistTrackCrossRef.class
}, version = 4, exportSchema = false)   // ← увеличили версию
public abstract class AppDatabase extends RoomDatabase  {

    public abstract TrackDao trackDao();
    public abstract PlaylistDao playlistDao();

    private static volatile AppDatabase instance;
    // Executor для фоновых операций с БД
    public static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "music_database"
                            ).fallbackToDestructiveMigration()   // разрушает старую БД при несовместимой миграции
                            .build();
                }
            }
        }
        return instance;
    }
}