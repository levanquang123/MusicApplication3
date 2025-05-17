package net.braniumacademy.musicapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.song.LocalSongDataSource
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
object SongRepositoryModule {
    @Provides
    fun provideSongRepository(localSongDataSource: LocalSongDataSource): SongRepositoryImpl {
        return SongRepositoryImpl(localSongDataSource)
    }
}