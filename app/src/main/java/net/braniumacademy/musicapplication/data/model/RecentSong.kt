package net.braniumacademy.musicapplication.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import net.braniumacademy.musicapplication.data.model.song.Song
import java.util.Date

@Entity(tableName = "recent_songs")
data class RecentSong(
    @ColumnInfo(name = "play_at")
    var playAt: Date = Date()
) : Song() {
    class Builder(song: Song) {
        init {
            recentSong = RecentSong()
            recentSong.id = song.id
            recentSong.title = song.title
            recentSong.artist = song.artist
            recentSong.album = song.album
            recentSong.duration = song.duration
            recentSong.source = song.source
            recentSong.image = song.image
            recentSong.favorite = song.favorite
            recentSong.counter = song.counter
            recentSong.replay = song.replay
            recentSong.playAt = Date()
        }

        fun build(): RecentSong {
            return recentSong
        }

        companion object {
            lateinit var recentSong: RecentSong
        }
    }
}