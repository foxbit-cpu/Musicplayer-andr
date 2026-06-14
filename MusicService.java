package com.example.musicplayerexam.player;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.TrackEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    public static final String ACTION_PLAY_PAUSE = "com.example.musicplayerexam.PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.example.musicplayerexam.NEXT";
    public static final String ACTION_PREVIOUS = "com.example.musicplayerexam.PREVIOUS";

    private final IBinder binder = new MusicBinder();
    private MediaPlayer mediaPlayer;
    private List<TrackEntity> playlist = new ArrayList<>();
    private int currentIndex = -1;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private MusicNotificationManager notificationManager;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        notificationManager = new MusicNotificationManager(this);
        setupMediaPlayerListeners();

        Notification notification = notificationManager.buildNotification(null, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(1, notification);
        }

        // Обработка входящих звонков
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm != null) {
                tm.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        if (state == TelephonyManager.CALL_STATE_RINGING && mediaPlayer != null && mediaPlayer.isPlaying()) {
                            playPause(); // ставим на паузу при звонке
                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    }

    private void setupMediaPlayerListeners() {
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isRepeat) {
                playTrack(currentIndex);
            } else {
                nextTrack();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY_PAUSE:
                    playPause();
                    break;
                case ACTION_NEXT:
                    nextTrack();
                    break;
                case ACTION_PREVIOUS:
                    previousTrack();
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setPlaylist(List<TrackEntity> playlist, int startIndex) {
        this.playlist.clear();
        this.playlist.addAll(playlist);
        this.currentIndex = startIndex;
        playTrack(currentIndex);
    }

    // Асинхронная подготовка MediaPlayer (предотвращает ANR)
    private void playTrack(int index) {
        if (index < 0 || index >= playlist.size()) return;
        TrackEntity track = playlist.get(index);
        try {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(track.getData());
            // Асинхронная подготовка
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                updateNotification(track);
            });
        } catch (IOException e) {
            Log.e("MusicService", "Ошибка подготовки трека", e);
        }
    }

    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            mediaPlayer.start();
            isPlaying = true;
        }
        updateNotification(getCurrentTrack());
    }

    public void nextTrack() {
        if (playlist.isEmpty()) return;
        if (isShuffle) {
            int newIndex = (int) (Math.random() * playlist.size());
            while (newIndex == currentIndex && playlist.size() > 1) {
                newIndex = (int) (Math.random() * playlist.size());
            }
            currentIndex = newIndex;
        } else {
            currentIndex = (currentIndex + 1) % playlist.size();
        }
        playTrack(currentIndex);
    }

    public void previousTrack() {
        if (playlist.isEmpty()) return;
        currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        playTrack(currentIndex);
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) mediaPlayer.seekTo(position);
    }

    public void toggleShuffle() {
        isShuffle = !isShuffle;
    }

    public void toggleRepeat() {
        isRepeat = !isRepeat;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public TrackEntity getCurrentTrack() {
        return (currentIndex >= 0 && currentIndex < playlist.size()) ? playlist.get(currentIndex) : null;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    private void updateNotification(TrackEntity track) {
        Notification notification = notificationManager.buildNotification(track, isPlaying);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        notificationManager.cancelNotification();
    }
}
