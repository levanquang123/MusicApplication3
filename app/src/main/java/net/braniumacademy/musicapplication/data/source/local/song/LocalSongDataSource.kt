package net.braniumacademy.musicapplication.data.source.local.song

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.source.SongDataSource
import javax.inject.Inject

class LocalSongDataSource @Inject constructor(
    private val songDao: SongDao
) : SongDataSource.Local {
    override val songs: Flow<List<Song>>
        get() = songDao.songs

    override val favoriteSongs: Flow<List<Song>>
        get() = songDao.favoriteSongs

    override val top15MostHeardSongs: Flow<List<Song>>
        get() = songDao.top15MostHeardSongs

    override val top40MostHeardSongs: Flow<List<Song>>
        get() = songDao.top40MostHeardSongs

    override val top15ForYouSongs: Flow<List<Song>>
        get() = songDao.top15ForYouSongs

    override val top40ForYouSongs: Flow<List<Song>>
        get() = songDao.top40ForYouSongs

    override val songPagingSource: PagingSource<Int, Song>
        get() = songDao.songPagingSource

    override suspend fun getSongsInAlbum(album: String): List<Song> {
        return songDao.getSongsInAlbum(album)
    }

    override fun getNSongPagingSource(limit: Int): PagingSource<Int, Song> {
        return songDao.getNSongPagingSource(limit)
    }

    override suspend fun getSongById(id: String): Song? {
        return songDao.getSongById(id)
    }

    override suspend fun insert(vararg songs: Song) {
        songDao.insert(*songs)
    }

    override suspend fun delete(song: Song) {
        songDao.delete(song)
    }

    override suspend fun update(song: Song) {
        songDao.update(song)
    }

    override suspend fun updateFavorite(id: String, favorite: Boolean) {
        songDao.updateFavorite(id, favorite)
    }
}