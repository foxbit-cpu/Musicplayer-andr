package com.example.musicplayerexam.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.musicplayerexam.data.entities.PlaylistEntity;
import com.example.musicplayerexam.data.entities.PlaylistTrackCrossRef;
import com.example.musicplayerexam.data.entities.TrackEntity;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert
    void insertPlaylist(PlaylistEntity playlist);

    @Delete
    void deletePlaylist(PlaylistEntity playlist);

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    LiveData<List<PlaylistEntity>> getAllPlaylists();

    @Insert
    void addTrackToPlaylist(PlaylistTrackCrossRef cross);

    @Query("DELETE FROM playlist_track_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    void removeTrackFromPlaylist(long playlistId, long trackId);

    @Query("SELECT t.* FROM tracks t INNER JOIN playlist_track_cross pt ON t.id = pt.trackId WHERE pt.playlistId = :playlistId")
    LiveData<List<TrackEntity>> getTracksForPlaylist(long playlistId);

    @Query("SELECT COUNT(*) FROM playlist_track_cross WHERE playlistId = :playlistId")
    int getTrackCountForPlaylist(long playlistId);

    @Query("DELETE FROM playlist_track_cross WHERE playlistId = :playlistId")
    void deleteCrossReferences(long playlistId);
}