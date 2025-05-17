package net.braniumacademy.musicapplication.data.source.local.searching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedSong
import net.braniumacademy.musicapplication.data.model.song.Song

@Dao
interface SearchingDao {
    @get:Query("SELECT * FROM history_searched_keys ORDER BY created_at DESC LIMIT 50")
    val allKeys: Flow<List<HistorySearchedKey>>

    @get:Query("SELECT * FROM history_searched_songs ORDER BY selected_at DESC LIMIT 50")
    val allSongs: Flow<List<HistorySearchedSong>>

    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query")
    fun search(query: String): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(keys: List<HistorySearchedKey>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(songs: List<HistorySearchedSong>)

    @Query("DELETE FROM history_searched_keys")
    suspend fun clearAllKeys()

    @Query("DELETE FROM history_searched_songs")
    suspend fun clearAllSongs()
}