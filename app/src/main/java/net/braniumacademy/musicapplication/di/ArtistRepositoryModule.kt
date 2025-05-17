package net.braniumacademy.musicapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.artist.LocalArtistDataSource
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ArtistRepositoryModule {
    @Provides
    fun provideArtistRepository(
        localArtistDataSource: LocalArtistDataSource
    ): ArtistRepositoryImpl {
        return ArtistRepositoryImpl(localArtistDataSource)
    }
}