package net.braniumacademy.musicapplication.data.source.local.artist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.braniumacademy.musicapplication.data.model.artist.ArtistRemoteKeys

@Dao
interface ArtistRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ArtistRemoteKeys>)

    @Query("SELECT * FROM artist_remote_keys WHERE artist_id = :artistId")
    suspend fun remoteKeysArtistId(artistId: Int): ArtistRemoteKeys?

    @Query("DELETE FROM artist_remote_keys")
    suspend fun clearAll()
}