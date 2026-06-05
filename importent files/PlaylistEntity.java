package com.example.musicplayerexam.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "playlists")
public class PlaylistEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private String genre;
    private boolean isPublic;
    private long createdAt;

    // Конструктор для создания нового плейлиста
    public PlaylistEntity(String name, String description, String genre, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.genre = genre;
        this.isPublic = isPublic;
        this.createdAt = System.currentTimeMillis();
    }

    // Пустой конструктор для Room
    public PlaylistEntity() {}

    // Геттеры и сеттеры
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}