package net.braniumacademy.musicapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.source.ArtistDataSource
import net.braniumacademy.musicapplication.data.source.local.artist.ArtistDao
import net.braniumacademy.musicapplication.data.source.local.artist.LocalArtistDataSource
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ArtistDataSourceModule {
    @Provides
    fun provideLocalArtistDataSource(artistDao: ArtistDao): ArtistDataSource.Local {
        return LocalArtistDataSource(artistDao)
    }
}