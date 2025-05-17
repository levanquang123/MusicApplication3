package net.braniumacademy.musicapplication.data.source

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistList
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.model.artist.ArtistWithSongs
import net.braniumacademy.musicapplication.data.source.remote.PagingParam

interface ArtistDataSource {
    interface Local {
        val artists: Flow<List<Artist>>

        val artistPagingSource: PagingSource<Int, Artist>

        fun getNArtistPagingSource(limit: Int): PagingSource<Int, Artist>

        fun getArtistWithSongs(artistId: Int): ArtistWithSongs

        val top15Artists: Flow<List<Artist>>

        fun getArtistById(id: Int): Flow<Artist?>

        suspend fun insert(vararg artists: Artist)

        suspend fun insertArtistSongCrossRef(vararg artistSongCrossRef: ArtistSongCrossRef)

        suspend fun delete(artist: Artist)

        suspend fun clearAll()

        suspend fun update(artist: Artist)
    }

    interface Remote {
        suspend fun loadArtists(callback: ResultCallback<Result<ArtistList>>)
        suspend fun loadArtistPaging(params: PagingParam): List<Artist>
    }
}