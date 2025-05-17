package net.braniumacademy.musicapplication.data.repository.artist

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistList
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.model.artist.ArtistWithSongs
import net.braniumacademy.musicapplication.data.source.ArtistDataSource
import net.braniumacademy.musicapplication.data.source.Result
import net.braniumacademy.musicapplication.data.source.remote.PagingParam
import net.braniumacademy.musicapplication.data.source.remote.RemoteArtistDataSource
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val localDataSource: ArtistDataSource.Local
) : ArtistRepository.Remote, ArtistRepository.Local {
    private val remoteArtistDataSource = RemoteArtistDataSource()

    override val artistPagingSource: PagingSource<Int, Artist>
        get() = localDataSource.artistPagingSource

    override fun getNArtistPagingSource(limit: Int): PagingSource<Int, Artist> {
        return localDataSource.getNArtistPagingSource(limit)
    }

    override val artists: Flow<List<Artist>>
        get() = localDataSource.artists

    override val top15Artists: Flow<List<Artist>>
        get() = localDataSource.top15Artists

    override fun getArtistWithSongs(artistId: Int): ArtistWithSongs {
        return localDataSource.getArtistWithSongs(artistId)
    }

    override fun getArtistById(id: Int): Flow<Artist?> {
        return localDataSource.getArtistById(id)
    }

    override suspend fun insert(vararg artists: Artist) {
        localDataSource.insert(*artists)
    }

    override suspend fun insertArtistSongCrossRef(vararg artistSongCrossRef: ArtistSongCrossRef) {
        localDataSource.insertArtistSongCrossRef(*artistSongCrossRef)
    }

    override suspend fun update(artist: Artist) {
        localDataSource.update(artist)
    }

    override suspend fun delete(artist: Artist) {
        localDataSource.delete(artist)
    }

    override suspend fun clearAll() {
        localDataSource.clearAll()
    }

    override suspend fun loadArtists(callback: ResultCallback<Result<ArtistList>>) {
        remoteArtistDataSource.loadArtists(callback)
    }

    override suspend fun loadArtistPaging(params: PagingParam): List<Artist> {
        return remoteArtistDataSource.loadArtistPaging(params)
    }
}