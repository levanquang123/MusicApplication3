package net.braniumacademy.musicapplication.data.model.song

import com.google.gson.annotations.SerializedName

data class SongList(
    @SerializedName("songs")
    val songs: List<Song> = emptyList()
)
