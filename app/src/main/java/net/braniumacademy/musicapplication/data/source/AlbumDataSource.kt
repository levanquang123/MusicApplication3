package net.braniumacademy.musicapplication.data.source

import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.album.Album

interface AlbumDataSource {
    interface Local {
        //  todo
    }

    interface Remote {
        suspend fun loadAlbums(callback: ResultCallback<Result<List<Album>>>)
    }
}