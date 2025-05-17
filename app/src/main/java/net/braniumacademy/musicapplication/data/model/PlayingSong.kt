package net.braniumacademy.musicapplication.data.model

import androidx.media3.common.MediaItem
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.song.Song

data class PlayingSong(
    private var _song: Song? = null,
    var playlist: Playlist? = null,
    var mediaItem: MediaItem? = null,
    var currentIndex: Int = -1
) {
    var song: Song?
        get() = _song
        set(value) {
            _song = value
            mediaItem = value?.source?.let { MediaItem.fromUri(it) }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayingSong) return false

        if (_song != other._song) return false
        if (playlist != other.playlist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _song?.hashCode() ?: 0
        result = 31 * result + (playlist?.hashCode() ?: 0)
        return result
    }
}
