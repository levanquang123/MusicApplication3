package net.braniumacademy.musicapplication.data.model.searching

import androidx.room.ColumnInfo
import androidx.room.Entity
import net.braniumacademy.musicapplication.data.model.song.Song
import java.util.Date

@Entity(tableName = "history_searched_songs")
data class HistorySearchedSong(
    @ColumnInfo(name = "selected_at") val selectedAt: Date
) : Song() {
    class Builder(private val song: Song) {
        private val historySearchedSong = HistorySearchedSong(Date())
        fun build(): HistorySearchedSong {
            historySearchedSong.id = song.id
            historySearchedSong.title = song.title
            historySearchedSong.artist = song.artist
            historySearchedSong.album = song.album
            historySearchedSong.duration = song.duration
            historySearchedSong.counter = song.counter
            historySearchedSong.image = song.image
            historySearchedSong.source = song.source
            historySearchedSong.replay = song.replay
            historySearchedSong.favorite = song.favorite
            return historySearchedSong
        }
    }
}