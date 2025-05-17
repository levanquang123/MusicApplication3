package net.braniumacademy.musicapplication.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.source.AlbumDataSource
import net.braniumacademy.musicapplication.data.source.Result

class RemoteAlbumDataSource : AlbumDataSource.Remote {
    override suspend fun loadAlbums(callback: ResultCallback<Result<List<Album>>>) {
        withContext(Dispatchers.IO) {
            val response = RetrofitHelper.instance.loadAlbums()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val albums = response.body()!!.albums
                    callback.onResult(Result.Success(albums))
                } else {
                    callback.onResult(Result.Error(Exception("Empty response")))
                }
            } else {
                callback.onResult(Result.Error(Exception(response.message())))
            }
        }
    }
}