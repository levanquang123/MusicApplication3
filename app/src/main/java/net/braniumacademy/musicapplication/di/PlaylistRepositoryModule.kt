package net.braniumacademy.musicapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.repository.playlist.PlaylistRepository
import net.braniumacademy.musicapplication.data.repository.playlist.PlaylistRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.playlist.LocalPlaylistDataSource
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object PlaylistRepositoryModule {
    @Provides
    fun providePlaylistRepository(
        localPlaylistDataSource: LocalPlaylistDataSource
    ): PlaylistRepository {
        return PlaylistRepositoryImpl(localPlaylistDataSource)
    }
}