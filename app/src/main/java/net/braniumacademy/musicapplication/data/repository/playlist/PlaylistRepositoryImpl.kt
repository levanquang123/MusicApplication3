package net.braniumacademy.musicapplication.data.repository.playlist

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistSongCrossRef
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistWithSongs
import net.braniumacademy.musicapplication.data.source.local.playlist.LocalPlaylistDataSource
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val localDataSource: LocalPlaylistDataSource
) : PlaylistRepository {
    override val playlists: Flow<List<Playlist>>
        get() = localDataSource.playlists

    override val allPlaylists: Flow<List<Playlist>>
        get() = localDataSource.allPlaylists

    override fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>> {
        return localDataSource.getAllPlaylistsWithSongs()
    }

    override fun getPlaylistsWithSongsByPlaylistId(playlistId: Int): Flow<PlaylistWithSongs> {
        return localDataSource.getPlaylistsWithSongsByPlaylistId(playlistId)
    }

    override suspend fun findPlaylistByName(name: String): Playlist? {
        return localDataSource.findPlaylistByName(name)
    }

    override suspend fun createPlaylist(playlist: Playlist) {
        localDataSource.createPlaylist(playlist)
    }

    override suspend fun createPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef): Long {
        return localDataSource.insertPlaylistSongCrossRef(playlistSongCrossRef)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        localDataSource.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        localDataSource.updatePlaylist(playlist)
    }
}