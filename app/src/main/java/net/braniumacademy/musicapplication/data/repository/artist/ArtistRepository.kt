package net.braniumacademy.musicapplication.data.repository.artist

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistList
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.model.artist.ArtistWithSongs
import net.braniumacademy.musicapplication.data.source.Result
import net.braniumacademy.musicapplication.data.source.remote.PagingParam

interface ArtistRepository {
    interface Local {
        val artists: Flow<List<Artist>>

        val artistPagingSource: PagingSource<Int, Artist>

        fun getNArtistPagingSource(limit: Int): PagingSource<Int, Artist>

        val top15Artists: Flow<List<Artist>>

        fun getArtistWithSongs(artistId: Int): ArtistWithSongs

        fun getArtistById(id: Int): Flow<Artist?>

        suspend fun insert(vararg artists: Artist)

        suspend fun insertArtistSongCrossRef(vararg artistSongCrossRef: ArtistSongCrossRef)

        suspend fun update(artist: Artist)

        suspend fun clearAll()

        suspend fun delete(artist: Artist)
    }

    interface Remote {
        suspend fun loadArtists(callback: ResultCallback<Result<ArtistList>>)
        suspend fun loadArtistPaging(params: PagingParam): List<Artist>
    }
}