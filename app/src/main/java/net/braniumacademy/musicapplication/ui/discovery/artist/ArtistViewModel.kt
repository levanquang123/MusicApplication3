package net.braniumacademy.musicapplication.ui.discovery.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRemoteMediator
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import javax.inject.Inject

class ArtistViewModel @Inject constructor(
    repository: ArtistRepositoryImpl,
    database: AppDatabase
) : ViewModel() {
    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>>
        get() = _artists

    fun setArtists(artists: List<Artist>) {
        _artists.value = artists
    }

    @OptIn(ExperimentalPagingApi::class)
    val artistFlow = Pager(
        PagingConfig(PAGE_SIZE),
        remoteMediator = ArtistRemoteMediator(repository, database)
    ) {
        repository.getNArtistPagingSource(15)
    }.flow

    class Factory @Inject constructor(
        private val repository: ArtistRepositoryImpl,
        private val database: AppDatabase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArtistViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ArtistViewModel(repository, database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}