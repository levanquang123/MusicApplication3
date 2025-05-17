package net.braniumacademy.musicapplication.data.model.song

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "songs")
open class Song(
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    var id: String = "",

    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title: String = "",

    @SerializedName("album")
    @ColumnInfo(name = "album")
    var album: String = "",

    @SerializedName("artist")
    @ColumnInfo(name = "artist")
    var artist: String = "",

    @SerializedName("source")
    @ColumnInfo(name = "source")
    var source: String = "",

    @SerializedName("image")
    @ColumnInfo(name = "image")
    var image: String = "",

    @SerializedName("duration")
    @ColumnInfo(name = "duration")
    var duration: Int = 0,

    @SerializedName("favorite")
    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,

    @SerializedName("counter")
    @ColumnInfo(name = "counter")
    var counter: Int = 0,

    @SerializedName("replay")
    @ColumnInfo(name = "replay")
    var replay: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Song) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}