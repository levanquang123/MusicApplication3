package net.braniumacademy.musicapplication.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.braniumacademy.musicapplication.data.source.local.AlbumDao
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import net.braniumacademy.musicapplication.data.source.local.artist.ArtistDao
import net.braniumacademy.musicapplication.data.source.local.artist.ArtistRemoteKeysDao
import net.braniumacademy.musicapplication.data.source.local.playlist.PlaylistDao
import net.braniumacademy.musicapplication.data.source.local.recent.RecentSongDao
import net.braniumacademy.musicapplication.data.source.local.searching.SearchingDao
import net.braniumacademy.musicapplication.data.source.local.song.SongDao
import net.braniumacademy.musicapplication.data.source.local.song.SongRemoteKeysDao
import net.braniumacademy.musicapplication.data.source.local.tracking.DBTrackingDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "music.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }

    @Provides
    fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao {
        return appDatabase.playlistDao()
    }

    @Provides
    fun provideAlbumDao(appDatabase: AppDatabase): AlbumDao {
        return appDatabase.albumDao()
    }

    @Provides
    fun provideRecentSongDao(appDatabase: AppDatabase): RecentSongDao {
        return appDatabase.recentSongDao()
    }

    @Provides
    fun provideArtistDao(appDatabase: AppDatabase): ArtistDao {
        return appDatabase.artistDao()
    }

    @Provides
    fun provideSongRemoteKeysDao(appDatabase: AppDatabase): SongRemoteKeysDao {
        return appDatabase.songRemoteKeysDao()
    }

    @Provides
    fun provideDbTrackingDao(appDatabase: AppDatabase): DBTrackingDao {
        return appDatabase.dbTrackingDao()
    }

    @Provides
    fun provideArtistRemoteKeysDao(appDatabase: AppDatabase): ArtistRemoteKeysDao {
        return appDatabase.artistRemoteKeysDao()
    }

    @Provides
    fun provideSearchingDao(appDatabase: AppDatabase): SearchingDao {
        return appDatabase.searchingDao()
    }
}