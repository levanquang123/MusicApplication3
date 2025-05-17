package net.braniumacademy.musicapplication.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistList
import net.braniumacademy.musicapplication.data.source.ArtistDataSource
import net.braniumacademy.musicapplication.data.source.Result

class RemoteArtistDataSource : ArtistDataSource.Remote {
    override suspend fun loadArtists(callback: ResultCallback<Result<ArtistList>>) {
        withContext(Dispatchers.IO) {
            val response = RetrofitHelper.instance.loadArtists()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val artistList = response.body()!!
                    callback.onResult(Result.Success(artistList))
                } else {
                    callback.onResult(Result.Error(Exception("Empty response")))
                }
            } else {
                callback.onResult(Result.Error(Exception(response.message())))
            }
        }
    }

    override suspend fun loadArtistPaging(params: PagingParam): List<Artist> {
        val response = RetrofitHelper.instance.loadArtistPaging(params)
        return response.body()?.artists ?: emptyList()
    }
}