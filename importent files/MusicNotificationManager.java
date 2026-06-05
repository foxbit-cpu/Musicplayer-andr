package com.example.musicplayerexam.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import com.example.musicplayerexam.MainActivity;
import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.TrackEntity;

public class MusicNotificationManager {
    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 1;
    private final Context context;
    private final NotificationManager notificationManager;

    public MusicNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Музыкальный плеер",
                    NotificationManager.IMPORTANCE_DEFAULT   // ← изменили с LOW на DEFAULT
            );
            channel.setDescription("Управление воспроизведением");
            channel.setShowBadge(false);
            // Включим звук и вибрацию (опционально)
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification buildNotification(TrackEntity track, boolean isPlaying) {
        String title = track != null ? track.getTitle() : "Не играет";
        String artist = track != null ? track.getArtist() : "";

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent playPauseIntent = new Intent(context, MusicService.class);
        playPauseIntent.setAction(MusicService.ACTION_PLAY_PAUSE);
        PendingIntent playPausePending = PendingIntent.getService(
                context, 0, playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent nextIntent = new Intent(context, MusicService.class);
        nextIntent.setAction(MusicService.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(
                context, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent prevIntent = new Intent(context, MusicService.class);
        prevIntent.setAction(MusicService.ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getService(
                context, 0, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(title)
                .setContentText(artist)
                .setContentIntent(mainPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // повышенный приоритет
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(isPlaying)
                .addAction(R.drawable.ic_previous, "Предыдущий", prevPending)
                .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                        isPlaying ? "Пауза" : "Старт", playPausePending)
                .addAction(R.drawable.ic_next, "Следующий", nextPending)
                .setStyle(new MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2));

        return builder.build();
    }

    public void showNotification(TrackEntity track, boolean isPlaying) {
        Notification notification = buildNotification(track, isPlaying);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}