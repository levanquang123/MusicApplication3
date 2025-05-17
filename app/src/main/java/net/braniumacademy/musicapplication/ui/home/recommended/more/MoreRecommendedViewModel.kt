package net.braniumacademy.musicapplication.ui.home.recommended.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRemoteMediator
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import javax.inject.Inject

class MoreRecommendedViewModel @Inject constructor(
    repository: SongRepositoryImpl,
    database: AppDatabase
) : ViewModel() {
    private val _recommendedSongs = MutableLiveData<List<Song>>()
    val recommendedSongs: LiveData<List<Song>>
        get() = _recommendedSongs

    fun setRecommendedSongs(songs: List<Song>) {
        _recommendedSongs.value = songs
    }

    @OptIn(ExperimentalPagingApi::class)
    val songFlow = Pager(
        PagingConfig(PAGE_SIZE),
        remoteMediator = SongRemoteMediator(repository, database)
    ) {
        repository.songPagingSource
    }.flow

    class Factory @Inject constructor (
        private val repository: SongRepositoryImpl,
        private val database: AppDatabase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MoreRecommendedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MoreRecommendedViewModel(repository, database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}