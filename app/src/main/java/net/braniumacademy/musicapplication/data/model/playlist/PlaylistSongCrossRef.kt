package net.braniumacademy.musicapplication.data.model.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_song_cross_ref",
    primaryKeys = ["playlist_id", "song_id"],
    indices = [Index("song_id"), Index("playlist_id")]
)
data class PlaylistSongCrossRef(
    @ColumnInfo(name = "playlist_id")
    var playlistId: Int = 0,

    @ColumnInfo(name = "song_id")
    var songId: String = ""
)