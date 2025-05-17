package net.braniumacademy.musicapplication.data.source.local.tracking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.braniumacademy.musicapplication.data.model.tracking.DBTracking

@Dao
interface DBTrackingDao {
    @get:Query("SELECT * FROM db_tracking WHERE last_song_updated <> 0 " +
            "ORDER BY last_song_updated DESC LIMIT 1")
    val tracking: DBTracking?

    @get:Query("SELECT * FROM db_tracking WHERE last_artist_updated <> 0 " +
            "ORDER BY last_artist_updated DESC LIMIT 1")
    val artistTracking: DBTracking?

    @Insert
    suspend fun insert(tracking: DBTracking)

    @Query("DELETE FROM db_tracking")
    suspend fun clearAll()
}