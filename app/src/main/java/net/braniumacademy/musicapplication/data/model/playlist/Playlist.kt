package net.braniumacademy.musicapplication.data.model.playlist

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import net.braniumacademy.musicapplication.data.model.song.Song
import java.util.Date

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    private var _id: Int = 10001,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "artwork")
    var artwork: String? = null,

    @ColumnInfo(name = "created_at")
    var createdAt: Date? = Date()
) {
    @Ignore
    private val _mediaItems: MutableList<MediaItem> = mutableListOf()

    @Ignore
    var songs: List<Song> = listOf()

    var id: Int
        get() = _id
        set(value) {
            _id = if (value > 0) {
                id
            } else {
                autoId++
            }
        }

    val mediaItems: List<MediaItem>
        get() = _mediaItems

    fun updateSongList(songs: List<Song>) {
        this.songs = songs
        updateMediaItems()
    }

    private fun updateMediaItems() {
        _mediaItems.clear()
        songs.forEach { song ->
            _mediaItems.add(MediaItem.fromUri(song.source))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false

        if (_id != other._id) return false

        return true
    }

    override fun hashCode(): Int {
        return _id
    }

    companion object {
        private var autoId = 10001
    }
}
