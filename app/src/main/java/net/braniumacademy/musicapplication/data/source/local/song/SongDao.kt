package net.braniumacademy.musicapplication.data.source.local.song

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.song.Song

@Dao
interface SongDao {
    @get: Query("SELECT * FROM songs")
    val songs: Flow<List<Song>>

    @get:Query("SELECT * FROM songs")
    val songPagingSource: PagingSource<Int, Song>

    @Query("SELECT * FROM songs WHERE album LIKE :album")
    suspend fun getSongsInAlbum(album: String): List<Song>

    @Query("SELECT * FROM songs LIMIT :limit")
    fun getNSongPagingSource(limit: Int): PagingSource<Int, Song>

    @get:Query("SELECT * FROM songs WHERE favorite = 1 LIMIT 15")
    val favoriteSongs: Flow<List<Song>>

    @get:Query("SELECT * FROM songs ORDER BY counter DESC LIMIT 15")
    val top15MostHeardSongs: Flow<List<Song>>

    @get:Query("SELECT * FROM songs ORDER BY counter DESC LIMIT 40")
    val top40MostHeardSongs: Flow<List<Song>>

    @get:Query("SELECT * FROM songs ORDER BY replay DESC LIMIT 15")
    val top15ForYouSongs: Flow<List<Song>>

    @get:Query("SELECT * FROM songs ORDER BY replay DESC LIMIT 40")
    val top40ForYouSongs: Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE song_id = :id")
    suspend fun getSongById(id: String): Song?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg songs: Song)

    @Delete
    suspend fun delete(song: Song)

    @Query("DELETE FROM songs")
    suspend fun clearAll()

    @Update
    suspend fun update(song: Song)

    @Query("UPDATE songs SET favorite = :favorite WHERE song_id = :id")
    suspend fun updateFavorite(id: String, favorite: Boolean)
}