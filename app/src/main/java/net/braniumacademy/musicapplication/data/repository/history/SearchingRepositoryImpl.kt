package net.braniumacademy.musicapplication.data.repository.history

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedSong
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.source.SearchingDataSource
import javax.inject.Inject

class SearchingRepositoryImpl @Inject constructor(
    private val dataSource: SearchingDataSource
) : SearchingRepository {
    override val allSongs: Flow<List<HistorySearchedSong>>
        get() = dataSource.allSongs

    override val allKeys: Flow<List<HistorySearchedKey>>
        get() = dataSource.allKeys

    override suspend fun search(query: String): Flow<List<Song>> {
        return dataSource.search(query)
    }

    override suspend fun insertKey(keys: List<HistorySearchedKey>) {
        return dataSource.insertKey(keys)
    }

    override suspend fun insertSong(songs: List<HistorySearchedSong>) {
        dataSource.insertSong(songs)
    }

    override suspend fun clearAllKeys() {
        dataSource.clearAllKeys()
    }

    override suspend fun clearAllSongs() {
        dataSource.clearAllSongs()
    }
}