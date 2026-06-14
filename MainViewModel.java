package com.example.musicplayerexam.viewmodel;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayerexam.data.AppDatabase;
import com.example.musicplayerexam.data.entities.AlbumEntity;
import com.example.musicplayerexam.data.entities.ArtistEntity;
import com.example.musicplayerexam.data.entities.PlaylistEntity;
import com.example.musicplayerexam.data.entities.PlaylistTrackCrossRef;
import com.example.musicplayerexam.data.entities.TrackEntity;
import com.example.musicplayerexam.player.MusicService;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MusicService musicService;
    private boolean isBound = false;

    // LiveData для состояния плеера
    private final MutableLiveData<TrackEntity> currentTrackLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlayingLive = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentPositionLive = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> durationLive = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> shuffleLive = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> repeatLive = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> audioSessionIdLive = new MutableLiveData<>(0);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
            startPolling();
            if (musicService.getCurrentTrack() != null) {
                currentTrackLive.postValue(musicService.getCurrentTrack());
                isPlayingLive.postValue(musicService.isPlaying());
                durationLive.postValue(musicService.getDuration());
                currentPositionLive.postValue(musicService.getCurrentPosition());
                shuffleLive.postValue(musicService.isShuffle());
                repeatLive.postValue(musicService.isRepeat());
                audioSessionIdLive.postValue(musicService.getAudioSessionId());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            stopPolling();
            musicService = null;
            isBound = false;
        }
    };

    public MainViewModel(Application application) {
        super(application);
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(getApplication(), MusicService.class);
        getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startPolling() {
        if (updateRunnable != null) return;
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isBound && musicService != null) {
                    TrackEntity current = musicService.getCurrentTrack();
                    if (current != null) {
                        currentTrackLive.postValue(current);
                        isPlayingLive.postValue(musicService.isPlaying());
                        durationLive.postValue(musicService.getDuration());
                        currentPositionLive.postValue(musicService.getCurrentPosition());
                        shuffleLive.postValue(musicService.isShuffle());
                        repeatLive.postValue(musicService.isRepeat());
                        audioSessionIdLive.postValue(musicService.getAudioSessionId());
                    }
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.post(updateRunnable);
    }

    private void stopPolling() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
            updateRunnable = null;
        }
    }

    // --- Управление воспроизведением ---
    public void setPlaylistAndPlay(List<TrackEntity> playlist, int startIndex) {
        if (isBound && musicService != null) {
            musicService.setPlaylist(playlist, startIndex);
        }
    }

    public void playPause() {
        if (isBound && musicService != null) musicService.playPause();
    }

    public void next() {
        if (isBound && musicService != null) musicService.nextTrack();
    }

    public void previous() {
        if (isBound && musicService != null) musicService.previousTrack();
    }

    public void seekTo(int position) {
        if (isBound && musicService != null) musicService.seekTo(position);
    }

    public void toggleShuffle() {
        if (isBound && musicService != null) {
            musicService.toggleShuffle();
            shuffleLive.setValue(musicService.isShuffle());
        }
    }

    public void toggleRepeat() {
        if (isBound && musicService != null) {
            musicService.toggleRepeat();
            repeatLive.setValue(musicService.isRepeat());
        }
    }

    // --- Работа с плейлистами (CRUD) ---
    public void createPlaylist(String name, String description, String genre, boolean isPublic) {
        AppDatabase db = AppDatabase.getInstance(getApplication());
        PlaylistEntity playlist = new PlaylistEntity(name, description, genre, isPublic);
        AppDatabase.databaseWriteExecutor.execute(() -> db.playlistDao().insertPlaylist(playlist));
    }

    public void deletePlaylist(PlaylistEntity playlist) {
        AppDatabase db = AppDatabase.getInstance(getApplication());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.playlistDao().deleteCrossReferences(playlist.getId());
            db.playlistDao().deletePlaylist(playlist);
        });
    }

    public void addTrackToPlaylist(long playlistId, long trackId) {
        AppDatabase db = AppDatabase.getInstance(getApplication());
        PlaylistTrackCrossRef cross = new PlaylistTrackCrossRef(playlistId, trackId);
        AppDatabase.databaseWriteExecutor.execute(() -> db.playlistDao().addTrackToPlaylist(cross));
    }

    // --- Группировка по альбомам и исполнителям (LiveData напрямую из DAO) ---
    public LiveData<List<AlbumEntity>> getAllAlbums() {
        return AppDatabase.getInstance(getApplication()).trackDao().getAllAlbums();
    }

    public LiveData<List<ArtistEntity>> getAllArtists() {
        return AppDatabase.getInstance(getApplication()).trackDao().getAllArtists();
    }

    public LiveData<List<TrackEntity>> getTracksForAlbum(String album) {
        return AppDatabase.getInstance(getApplication()).trackDao().getTracksForAlbum(album);
    }

    public LiveData<List<TrackEntity>> getTracksForArtist(String artist) {
        return AppDatabase.getInstance(getApplication()).trackDao().getTracksForArtist(artist);
    }

    // --- Геттеры для LiveData (прямые запросы к БД) ---
    public LiveData<List<TrackEntity>> getAllTracks() {
        return AppDatabase.getInstance(getApplication()).trackDao().getAllTracks();
    }

    public LiveData<List<PlaylistEntity>> getAllPlaylists() {
        return AppDatabase.getInstance(getApplication()).playlistDao().getAllPlaylists();
    }

    public LiveData<List<TrackEntity>> getTracksForPlaylist(long playlistId) {
        return AppDatabase.getInstance(getApplication()).playlistDao().getTracksForPlaylist(playlistId);
    }

    // --- Геттеры для состояния плеера ---
    public LiveData<TrackEntity> getCurrentTrack() { return currentTrackLive; }
    public LiveData<Boolean> isPlaying() { return isPlayingLive; }
    public LiveData<Integer> getCurrentPosition() { return currentPositionLive; }
    public LiveData<Integer> getDuration() { return durationLive; }
    public LiveData<Boolean> getShuffle() { return shuffleLive; }
    public LiveData<Boolean> getRepeat() { return repeatLive; }
    public LiveData<Integer> getAudioSessionId() { return audioSessionIdLive; }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling();
        if (isBound) {
            getApplication().unbindService(serviceConnection);
        }
    }
}