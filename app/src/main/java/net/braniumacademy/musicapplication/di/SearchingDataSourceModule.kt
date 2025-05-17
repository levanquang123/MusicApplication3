package net.braniumacademy.musicapplication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.braniumacademy.musicapplication.data.source.SearchingDataSource
import net.braniumacademy.musicapplication.data.source.local.searching.LocalSearchingDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchingDataSourceModule {
    @Binds
    abstract fun bindSearchingDataSource(
        localSearchingDataSource: LocalSearchingDataSource
    ): SearchingDataSource
}