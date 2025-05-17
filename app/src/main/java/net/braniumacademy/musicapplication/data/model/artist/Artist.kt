package net.braniumacademy.musicapplication.data.model.artist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "artists")
data class Artist(
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "artist_id")
    val id: Int,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar")
    val avatar: String,

    @SerializedName("interested")
    @ColumnInfo(name = "interested")
    val interested: Int,

    @Transient
    @ColumnInfo(name = "care_about")
    var isCareAbout: Boolean = false
)