package net.braniumacademy.musicapplication.data.source.local.playlist

import android.database.sqlite.SQLiteConstraintException
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistSongCrossRef
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistWithSongs
import net.braniumacademy.musicapplication.data.source.PlaylistDataSource
import javax.inject.Inject

class LocalPlaylistDataSource @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistDataSource {
    override val playlists: Flow<List<Playlist>>
        get() = playlistDao.playlists

    override val allPlaylists: Flow<List<Playlist>>
        get() = playlistDao.allPlaylists

    override fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>> {
        return playlistDao.getAllPlaylistsWithSongs()
    }

    override fun getPlaylistsWithSongsByPlaylistId(playlistId: Int): Flow<PlaylistWithSongs> {
        return playlistDao.getPlaylistsWithSongsByPlaylistId(playlistId)
    }

    override suspend fun findPlaylistByName(name: String): Playlist? {
        return playlistDao.findPlaylistByName(name)
    }

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insert(playlist)
    }

    override suspend fun insertPlaylistSongCrossRef(obj: PlaylistSongCrossRef): Long {
        return try {
            playlistDao.insertPlaylistSongCrossRef(obj)
        } catch (e: SQLiteConstraintException) {
            -1L
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.delete(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(playlist)
    }
}