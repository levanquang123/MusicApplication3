package net.braniumacademy.musicapplication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.source.PlaylistDataSource
import net.braniumacademy.musicapplication.data.source.local.playlist.LocalPlaylistDataSource

@Module
@InstallIn(ActivityComponent::class)
abstract class PlaylistDataSourceModule {

    @Binds
    abstract fun provideLocalPlaylistDataSource(
        dataSource: LocalPlaylistDataSource
    ): PlaylistDataSource
}