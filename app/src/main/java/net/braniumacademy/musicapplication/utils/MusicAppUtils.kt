package net.braniumacademy.musicapplication.utils

import net.braniumacademy.musicapplication.data.model.song.Song

object MusicAppUtils {
    const val EXTRA_CURRENT_FRACTION = "EXTRA_CURRENT_FRACTION"
    const val DEFAULT_MARGIN_END = 48
    const val CACHE_TIMEOUT = 168L
    @JvmField
    var isConfigChanged = false

    @JvmField
    var DENSITY: Float = 0f

    enum class DefaultPlaylistName(val value: String) {
        DEFAULT("Default"),
        FAVORITES("Favorites"),
        RECOMMENDED("Recommended"),
        RECENT("Recent"),
        SEARCH("Search"),
        MOST_HEARD("Most_Heard"),
        FOR_YOU("For_You"),
        CUSTOM("Custom")
    }

    fun getSongIndex(song: Song, songs: List<Song>): Int {
        val index = songs.indexOf(song)
        return if (index == -1) return 0 else index
    }
}