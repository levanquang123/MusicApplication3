package net.braniumacademy.musicapplication.data.repository.song

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongList
import net.braniumacademy.musicapplication.data.source.Result
import net.braniumacademy.musicapplication.data.source.local.song.LocalSongDataSource
import net.braniumacademy.musicapplication.data.source.remote.PagingParam
import net.braniumacademy.musicapplication.data.source.remote.RemoteSongDataSource
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val localDataSource: LocalSongDataSource
) : SongRepository.Remote, SongRepository.Local {
    private val remoteSongDataSource = RemoteSongDataSource()
    override suspend fun loadSongs(callback: ResultCallback<Result<SongList>>) {
        remoteSongDataSource.loadSongs(callback)
    }

    override val songs: Flow<List<Song>>
        get() = localDataSource.songs

    override val favoriteSongs: Flow<List<Song>>
        get() = localDataSource.favoriteSongs

    override val top15MostHeardSongs: Flow<List<Song>>
        get() = localDataSource.top15MostHeardSongs

    override val top40MostHeardSongs: Flow<List<Song>>
        get() = localDataSource.top40MostHeardSongs

    override val top15ForYouSongs: Flow<List<Song>>
        get() = localDataSource.top15ForYouSongs

    override val top40ForYouSongs: Flow<List<Song>>
        get() = localDataSource.top40ForYouSongs

    override val songPagingSource: PagingSource<Int, Song>
        get() = localDataSource.songPagingSource

    override suspend fun getSongsInAlbum(album: String): List<Song> {
        return localDataSource.getSongsInAlbum(album)
    }

    override fun getNSongPagingSource(limit: Int): PagingSource<Int, Song> {
        return localDataSource.getNSongPagingSource(limit)
    }

    override suspend fun getSongById(id: String): Song? {
        return localDataSource.getSongById(id)
    }

    override suspend fun insert(vararg songs: Song) {
        localDataSource.insert(*songs)
    }

    override suspend fun delete(song: Song) {
        localDataSource.delete(song)
    }

    override suspend fun update(song: Song) {
        localDataSource.update(song)
    }

    override suspend fun updateFavorite(id: String, favorite: Boolean) {
        localDataSource.updateFavorite(id, favorite)
    }

    override suspend fun loadSongPaging(param: PagingParam): List<Song> {
        return remoteSongDataSource.loadSongPaging(param)
    }
}