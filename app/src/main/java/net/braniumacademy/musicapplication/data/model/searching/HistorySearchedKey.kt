package net.braniumacademy.musicapplication.data.model.searching

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "history_searched_keys",
    indices = [Index(value = ["key"], unique = true)]
)
data class HistorySearchedKey(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "key")
    var key: String = "",

    @ColumnInfo(name = "created_at")
    var createdAt: Date = Date()
)
