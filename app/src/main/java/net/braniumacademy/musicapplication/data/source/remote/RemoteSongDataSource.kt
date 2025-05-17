package net.braniumacademy.musicapplication.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongList
import net.braniumacademy.musicapplication.data.source.Result
import net.braniumacademy.musicapplication.data.source.SongDataSource

class RemoteSongDataSource : SongDataSource.Remote {
    override suspend fun loadSongs(callback: ResultCallback<Result<SongList>>) {
        withContext(Dispatchers.IO) {
            val response = RetrofitHelper.instance.loadSongs()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val songList = response.body()!!
                    callback.onResult(Result.Success(songList))
                } else {
                    callback.onResult(Result.Error(Exception("Empty response")))
                }
            } else {
                callback.onResult(Result.Error(Exception(response.message())))
            }
        }
    }

    override suspend fun loadSongPaging(params: PagingParam): List<Song> {
        val response = RetrofitHelper.instance.loadSongPaging(params)
        return response.body()?.songs ?: emptyList()
    }
}