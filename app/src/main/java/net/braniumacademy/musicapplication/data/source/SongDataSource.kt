package net.braniumacademy.musicapplication.data.source

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongList
import net.braniumacademy.musicapplication.data.source.remote.PagingParam

interface SongDataSource {
    interface Local {
        val songs: Flow<List<Song>>

        val favoriteSongs: Flow<List<Song>>

        val top15MostHeardSongs: Flow<List<Song>>

        val top40MostHeardSongs: Flow<List<Song>>

        val top15ForYouSongs: Flow<List<Song>>

        val top40ForYouSongs: Flow<List<Song>>

        val songPagingSource: PagingSource<Int, Song>

        suspend fun getSongsInAlbum(album: String): List<Song>

        fun getNSongPagingSource(limit: Int): PagingSource<Int, Song>

        suspend fun getSongById(id: String): Song?

        suspend fun insert(vararg songs: Song)

        suspend fun delete(song: Song)

        suspend fun update(song: Song)

        suspend fun updateFavorite(id: String, favorite: Boolean)
    }

    interface Remote {
        suspend fun loadSongs(callback: ResultCallback<Result<SongList>>)
        suspend fun loadSongPaging(params: PagingParam): List<Song>
    }
}