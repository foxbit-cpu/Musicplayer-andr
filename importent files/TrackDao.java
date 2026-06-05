package com.example.musicplayerexam.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.musicplayerexam.data.entities.AlbumEntity;
import com.example.musicplayerexam.data.entities.ArtistEntity;
import com.example.musicplayerexam.data.entities.TrackEntity;
import java.util.List;

@Dao
public interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TrackEntity> tracks);

    @Query("DELETE FROM tracks")
    void deleteAll();

    @Query("SELECT * FROM tracks ORDER BY title ASC")
    LiveData<List<TrackEntity>> getAllTracks();

    @Query("SELECT * FROM tracks WHERE artist = :artist ORDER BY title ASC")
    LiveData<List<TrackEntity>> getTracksByArtist(String artist);

    @Query("SELECT * FROM tracks WHERE album = :album ORDER BY trackNumber ASC")
    LiveData<List<TrackEntity>> getTracksByAlbum(String album);

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    LiveData<List<TrackEntity>> searchTracks(String query);

    @Query("SELECT * FROM tracks WHERE id = :id")
    LiveData<TrackEntity> getTrackById(long id);

    @Query("SELECT album, artist, COUNT(*) as trackCount FROM tracks GROUP BY album, artist ORDER BY album")
    LiveData<List<AlbumEntity>> getAllAlbums();

    @Query("SELECT artist, COUNT(*) as trackCount FROM tracks GROUP BY artist ORDER BY artist")
    LiveData<List<ArtistEntity>> getAllArtists();

    // --- Дополнительные запросы для детализации ---
    @Query("SELECT * FROM tracks WHERE album = :album ORDER BY trackNumber ASC")
    LiveData<List<TrackEntity>> getTracksForAlbum(String album);

    @Query("SELECT * FROM tracks WHERE artist = :artist ORDER BY title ASC")
    LiveData<List<TrackEntity>> getTracksForArtist(String artist);
}