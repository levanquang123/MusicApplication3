package net.braniumacademy.musicapplication.data.repository

import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.source.Result
import net.braniumacademy.musicapplication.data.source.remote.RemoteAlbumDataSource
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor() : AlbumRepository.Local, AlbumRepository.Remote {
    private val remoteAlbumDataSource = RemoteAlbumDataSource()

    override suspend fun loadAlbums(callback: ResultCallback<Result<List<Album>>>) {
        remoteAlbumDataSource.loadAlbums(callback)
    }
}