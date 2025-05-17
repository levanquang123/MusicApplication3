package net.braniumacademy.musicapplication.data.source.local.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.braniumacademy.musicapplication.data.model.song.SongRemoteKeys

@Dao
interface SongRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<SongRemoteKeys>)

    @Query("SELECT * FROM song_remote_keys WHERE song_id = :songId")
    suspend fun remoteKeysSongId(songId: String): SongRemoteKeys?

    @Query("DELETE FROM song_remote_keys")
    suspend fun clearAll()
}