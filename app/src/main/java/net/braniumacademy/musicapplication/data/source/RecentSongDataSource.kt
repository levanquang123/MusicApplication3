package net.braniumacademy.musicapplication.data.source

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.song.Song

interface RecentSongDataSource {
    interface Local {
        val recentSongs: Flow<List<Song>>
        suspend fun insert(vararg recentSongs: RecentSong)
    }

    interface Remote {
        // TODO
    }
}