package com.example.musicplayerexam.data.scanner;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musicplayerexam.data.AppDatabase;
import com.example.musicplayerexam.data.entities.TrackEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaScanner {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void scanAllAudio(Context context) {
        executor.execute(() -> {
            List<TrackEntity> trackList = new ArrayList<>();

            Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TRACK
            };
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            try (Cursor cursor = context.getContentResolver().query(
                    collection, projection, selection, null, null)) {

                if (cursor != null) {
                    int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                    int durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int trackIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);

                    while (cursor.moveToNext()) {
                        String title = cursor.getString(titleIdx);
                        if (title == null || title.trim().isEmpty()) title = "Неизвестно";
                        String artist = cursor.getString(artistIdx);
                        if (artist == null || artist.trim().isEmpty()) artist = "Неизвестный исполнитель";
                        String album = cursor.getString(albumIdx);
                        if (album == null || album.trim().isEmpty()) album = "Неизвестный альбом";
                        String data = cursor.getString(dataIdx);
                        long duration = cursor.getLong(durationIdx);
                        int trackNumber = cursor.getInt(trackIdx);

                        TrackEntity track = new TrackEntity(title, artist, album, data, duration, trackNumber);
                        trackList.add(track);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!trackList.isEmpty()) {
                AppDatabase db = AppDatabase.getInstance(context);
                // !!! ВАЖНО: удаляем старые треки, чтобы избежать дублирования
                db.trackDao().deleteAll();
                db.trackDao().insertAll(trackList);
            }
        });
    }
}