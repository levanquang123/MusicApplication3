package net.braniumacademy.musicapplication.data.model.artist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import net.braniumacademy.musicapplication.data.model.song.Song

data class ArtistWithSongs(
    @Embedded
    val artist: Artist? = null,

    @Relation(
        parentColumn = "artist_id",
        entityColumn = "song_id",
        associateBy = Junction(ArtistSongCrossRef::class)
    )
    val songs: List<Song> = emptyList()
)
