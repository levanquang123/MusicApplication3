package net.braniumacademy.musicapplication.ui.home.recommended

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRemoteMediator
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import javax.inject.Inject

@HiltViewModel
class RecommendedViewModel @Inject constructor(
    repository: SongRepositoryImpl,
    database: AppDatabase
) : ViewModel() {
    val songs: LiveData<List<Song>> = repository.songs.asLiveData()

    @OptIn(ExperimentalPagingApi::class)
    val songFlow = Pager(
        PagingConfig(PAGE_SIZE),
        remoteMediator = SongRemoteMediator(repository, database)
    ) {
        repository.getNSongPagingSource(20)
    }.flow

    class Factory @Inject constructor(
        private val repository: SongRepositoryImpl,
        private val database: AppDatabase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecommendedViewModel(repository, database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}