package net.braniumacademy.musicapplication.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistRemoteKeys
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistSongCrossRef
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedSong
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongRemoteKeys
import net.braniumacademy.musicapplication.data.model.tracking.DBTracking
import net.braniumacademy.musicapplication.data.source.local.artist.ArtistDao
import net.braniumacademy.musicapplication.data.source.local.artist.ArtistRemoteKeysDao
import net.braniumacademy.musicapplication.data.source.local.playlist.PlaylistDao
import net.braniumacademy.musicapplication.data.source.local.recent.RecentSongDao
import net.braniumacademy.musicapplication.data.source.local.searching.SearchingDao
import net.braniumacademy.musicapplication.data.source.local.song.SongDao
import net.braniumacademy.musicapplication.data.source.local.song.SongRemoteKeysDao
import net.braniumacademy.musicapplication.data.source.local.tracking.DBTrackingDao

@Database(
    entities = [
        Song::class,
        Playlist::class,
        Album::class,
        RecentSong::class,
        PlaylistSongCrossRef::class,
        Artist::class,
        ArtistSongCrossRef::class,
        SongRemoteKeys::class,
        DBTracking::class,
        ArtistRemoteKeys::class,
        HistorySearchedKey::class,
        HistorySearchedSong::class
    ],
    version = 10,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 9, to = 10, spec = AppDatabase.MigrationSpec::class)
    ]
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun albumDao(): AlbumDao
    abstract fun recentSongDao(): RecentSongDao
    abstract fun artistDao(): ArtistDao
    abstract fun songRemoteKeysDao(): SongRemoteKeysDao
    abstract fun dbTrackingDao(): DBTrackingDao
    abstract fun artistRemoteKeysDao(): ArtistRemoteKeysDao
    abstract fun searchingDao(): SearchingDao

    @RenameColumn(
        tableName = "artists",
        fromColumnName = "id",
        toColumnName = "artist_id"
    )
    internal class MigrationSpec : AutoMigrationSpec
}