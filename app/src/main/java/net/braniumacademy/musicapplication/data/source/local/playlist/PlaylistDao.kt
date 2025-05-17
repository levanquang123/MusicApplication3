package net.braniumacademy.musicapplication.data.source.local.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistSongCrossRef
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistWithSongs

@Dao
interface PlaylistDao {
    @get:Query("SELECT * FROM playlists LIMIT 10")
    val playlists: Flow<List<Playlist>>

    @get:Query("SELECT * FROM playlists")
    val allPlaylists: Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    fun getPlaylistsWithSongsByPlaylistId(playlistId: Int): Flow<PlaylistWithSongs>

    @Query("SELECT * FROM playlists WHERE name = :name")
    suspend fun findPlaylistByName(name: String): Playlist?

    @Insert
    suspend fun insert(playlist: Playlist)

    @Insert
    suspend fun insertPlaylistSongCrossRef(obj: PlaylistSongCrossRef): Long

    @Delete
    suspend fun delete(playlist: Playlist)

    @Update
    suspend fun update(playlist: Playlist)
}