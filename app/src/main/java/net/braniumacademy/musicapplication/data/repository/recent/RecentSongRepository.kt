package net.braniumacademy.musicapplication.data.repository.recent

import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.song.Song

interface RecentSongRepository {
    interface Local {
        val recentSongs: Flow<List<Song>>

        suspend fun insert(vararg recentSongs: RecentSong)
    }

    interface Remote {
        // todo
    }
}