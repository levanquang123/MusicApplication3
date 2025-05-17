package net.braniumacademy.musicapplication.data.source.local.recent

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.source.RecentSongDataSource
import javax.inject.Inject

class LocalRecentSongDataSource @Inject constructor(
    private val recentSongDao: RecentSongDao
) : RecentSongDataSource.Local {
    override val recentSongs: Flow<List<Song>>
        get() = recentSongDao.recentSongs

    override suspend fun insert(vararg recentSongs: RecentSong) {
        recentSongDao.insert(*recentSongs)
    }
}