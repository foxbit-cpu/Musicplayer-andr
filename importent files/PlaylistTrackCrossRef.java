package com.example.musicplayerexam.data.entities;

import androidx.room.Entity;

@Entity(tableName = "playlist_track_cross", primaryKeys = {"playlistId", "trackId"})
public class PlaylistTrackCrossRef {
    public long playlistId;
    public long trackId;

    public PlaylistTrackCrossRef(long playlistId, long trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }
}