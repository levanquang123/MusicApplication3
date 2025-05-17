package net.braniumacademy.musicapplication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.braniumacademy.musicapplication.data.repository.history.SearchingRepository
import net.braniumacademy.musicapplication.data.repository.history.SearchingRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchingRepositoryModule {
    @Binds
    abstract fun bindSearchingRepository(
        searchingRepositoryImpl: SearchingRepositoryImpl
    ): SearchingRepository
}