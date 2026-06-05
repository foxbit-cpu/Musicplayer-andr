package com.example.musicplayerexam.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracks")
public class TrackEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String artist;
    private String album;
    private String data;      // полный путь к файлу
    private long duration;    // в миллисекундах
    private int trackNumber;

    // Конструктор с обязательными полями
    public TrackEntity(String title, String artist, String album, String data, long duration, int trackNumber) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.duration = duration;
        this.trackNumber = trackNumber;
    }

    // Пустой конструктор для Room
    public TrackEntity() {}

    // Геттеры и сеттеры
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public int getTrackNumber() { return trackNumber; }
    public void setTrackNumber(int trackNumber) { this.trackNumber = trackNumber; }
}