package net.braniumacademy.musicapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.repository.recent.RecentSongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.recent.LocalRecentSongDataSource
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object RecentSongRepositoryModule {
    @Provides
    fun provideRecentSongRepository(
        localRecentSongDataSource: LocalRecentSongDataSource
    ): RecentSongRepositoryImpl {
        return RecentSongRepositoryImpl(localRecentSongDataSource)
    }
}