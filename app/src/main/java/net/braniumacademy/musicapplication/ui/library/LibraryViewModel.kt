package net.braniumacademy.musicapplication.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.playlist.PlaylistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.recent.RecentSongRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    recentSongRepository: RecentSongRepositoryImpl,
    songRepository: SongRepositoryImpl,
    playlistRepository: PlaylistRepositoryImpl
) : ViewModel() {
    val recentSongs: LiveData<List<Song>> = recentSongRepository.recentSongs.asLiveData()
    val favoriteSongs: LiveData<List<Song>> = songRepository.favoriteSongs.asLiveData()

    class Factory @Inject constructor (
        private val recentSongRepository: RecentSongRepositoryImpl,
        private val songRepository: SongRepositoryImpl,
        private val playlistRepository: PlaylistRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LibraryViewModel(
                    recentSongRepository,
                    songRepository,
                    playlistRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}