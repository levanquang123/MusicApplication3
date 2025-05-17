package net.braniumacademy.musicapplication.data.source

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedSong
import net.braniumacademy.musicapplication.data.model.song.Song

interface SearchingDataSource {
    val allSongs: Flow<List<HistorySearchedSong>>

    val allKeys: Flow<List<HistorySearchedKey>>

    suspend fun search(query: String): Flow<List<Song>>

    suspend fun insertKey(keys: List<HistorySearchedKey>)

    suspend fun insertSong(songs: List<HistorySearchedSong>)

    suspend fun clearAllKeys()

    suspend fun clearAllSongs()
}