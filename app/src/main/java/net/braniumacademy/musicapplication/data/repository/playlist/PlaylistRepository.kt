package net.braniumacademy.musicapplication.data.repository.playlist

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistSongCrossRef
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistWithSongs

interface PlaylistRepository {
    val playlists: Flow<List<Playlist>>
    val allPlaylists: Flow<List<Playlist>>
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>
    fun getPlaylistsWithSongsByPlaylistId(playlistId: Int): Flow<PlaylistWithSongs>
    suspend fun findPlaylistByName(name: String): Playlist?
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun createPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef): Long
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}