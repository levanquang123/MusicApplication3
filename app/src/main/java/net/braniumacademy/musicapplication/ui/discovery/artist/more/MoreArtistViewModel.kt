package net.braniumacademy.musicapplication.ui.discovery.artist.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRemoteMediator
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import javax.inject.Inject

class MoreArtistViewModel @Inject constructor(
    artistRepository: ArtistRepositoryImpl,
    database: AppDatabase
) : ViewModel() {
    val artists = artistRepository.artists.asLiveData()

    @OptIn(ExperimentalPagingApi::class)
    val artistFlow = Pager(
        PagingConfig(PAGE_SIZE),
        remoteMediator = ArtistRemoteMediator(artistRepository, database)
    ) {
        artistRepository.artistPagingSource
    }.flow

    class Factory @Inject constructor(
        private val artistRepository: ArtistRepositoryImpl,
        private val database: AppDatabase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MoreArtistViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MoreArtistViewModel(artistRepository, database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}