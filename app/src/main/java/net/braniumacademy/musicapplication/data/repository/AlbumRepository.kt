package net.braniumacademy.musicapplication.data.repository

import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.source.Result

interface AlbumRepository {
    interface Local {
        // todo
    }

    interface Remote {
        suspend fun loadAlbums(callback: ResultCallback<Result<List<Album>>>)
    }
}