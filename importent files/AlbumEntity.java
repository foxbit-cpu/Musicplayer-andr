package com.example.musicplayerexam.data.entities;

public class AlbumEntity {
    public String album;
    public String artist;
    public int trackCount;

    public AlbumEntity() {}

    public AlbumEntity(String album, String artist, int trackCount) {
        this.album = album;
        this.artist = artist;
        this.trackCount = trackCount;
    }

    public String getAlbum() { return album; }
    public String getArtist() { return artist; }
    public int getTrackCount() { return trackCount; }
}