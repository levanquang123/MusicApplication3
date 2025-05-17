package net.braniumacademy.musicapplication.data.model.album

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "albums")
data class Album(
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "album_id")
    var id: Int = 0,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String = "",

    @SerializedName("songs")
    @Ignore
    var songs: List<String> = emptyList(),

    @SerializedName("size")
    @ColumnInfo(name = "size")
    var size: Int = 0,

    @SerializedName("artwork")
    @ColumnInfo(name = "artwork")
    var artwork: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Album) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
