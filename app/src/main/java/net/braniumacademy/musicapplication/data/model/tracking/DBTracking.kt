package net.braniumacademy.musicapplication.data.model.tracking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "db_tracking")
data class DBTracking(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "last_song_updated", defaultValue = "0")
    val lastSongUpdated: Long,

    @ColumnInfo(name = "last_artist_updated", defaultValue = "0")
    val lastArtistUpdated: Long
)
