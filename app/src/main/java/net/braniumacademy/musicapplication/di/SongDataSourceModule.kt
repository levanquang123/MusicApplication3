package net.braniumacademy.musicapplication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.source.SongDataSource
import net.braniumacademy.musicapplication.data.source.local.song.LocalSongDataSource

@Module
@InstallIn(ActivityComponent::class)
abstract class SongDataSourceModule {
    @Binds
    abstract fun provideSongDataSource(songDataSource: LocalSongDataSource): SongDataSource.Local
}