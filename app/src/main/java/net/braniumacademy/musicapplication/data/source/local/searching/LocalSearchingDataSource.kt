package net.braniumacademy.musicapplication.data.source.local.searching

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedSong
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.source.SearchingDataSource
import javax.inject.Inject

class LocalSearchingDataSource @Inject constructor(
    private val searchingDao: SearchingDao
) : SearchingDataSource {
    override val allSongs: Flow<List<HistorySearchedSong>>
        get() = searchingDao.allSongs

    override val allKeys: Flow<List<HistorySearchedKey>>
        get() = searchingDao.allKeys

    override suspend fun search(query: String): Flow<List<Song>> {
        val keyToSearch = "%$query%"
        return searchingDao.search(keyToSearch)
    }

    override suspend fun insertKey(keys: List<HistorySearchedKey>) {
        return searchingDao.insertKey(keys)
    }

    override suspend fun insertSong(songs: List<HistorySearchedSong>) {
        searchingDao.insertSong(songs)
    }

    override suspend fun clearAllKeys() {
        searchingDao.clearAllKeys()
    }

    override suspend fun clearAllSongs() {
        searchingDao.clearAllSongs()
    }
}