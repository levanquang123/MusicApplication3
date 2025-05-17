package net.braniumacademy.musicapplication.data.model.artist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "artist_song_cross_ref",
    primaryKeys = ["song_id", "artist_id"],
    indices = [Index("artist_id"), Index("song_id")]
)
data class ArtistSongCrossRef(
    @ColumnInfo(name = "song_id")
    val songId: String,

    @ColumnInfo(name = "artist_id")
    val artistId: Int
)
