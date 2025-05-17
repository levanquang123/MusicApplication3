package net.braniumacademy.musicapplication.data.source.local.artist

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.model.artist.ArtistWithSongs
import net.braniumacademy.musicapplication.data.model.song.Song

@Dao
interface ArtistDao {
    @get:Query("SELECT * FROM artists")
    val artists: Flow<List<Artist>>

    @get:Query("SELECT * FROM artists")
    val artistPagingSource: PagingSource<Int, Artist>

    @Query("SELECT * FROM artists LIMIT :limit")
    fun getNArtistPagingSource(limit: Int): PagingSource<Int, Artist>

    @get:Query("SELECT * FROM artists LIMIT 15")
    val top15Artists: Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM artists WHERE artist_id = :artistId")
    fun getArtistWithSongs(artistId: Int): ArtistWithSongs

    @Query("SELECT * FROM artists WHERE artist_id = :id")
    fun getArtistById(id: Int): Flow<Artist?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg artists: Artist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtistSongCrossRef(vararg artistSongCrossRef: ArtistSongCrossRef)

    @Delete
    suspend fun delete(artist: Artist)

    @Query("DELETE FROM artists")
    suspend fun clearAll()

    @Update
    suspend fun update(artist: Artist)
}