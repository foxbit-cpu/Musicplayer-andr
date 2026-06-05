package com.example.musicplayerexam.data.entities;

public class ArtistEntity {
    public String artist;
    public int trackCount;

    public ArtistEntity() {}

    public ArtistEntity(String artist, int trackCount) {
        this.artist = artist;
        this.trackCount = trackCount;
    }

    public String getArtist() { return artist; }
    public int getTrackCount() { return trackCount; }
}