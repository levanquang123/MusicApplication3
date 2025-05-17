package net.braniumacademy.musicapplication.ui.discovery.foryou

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import javax.inject.Inject

class ForYouViewModel @Inject constructor(
    songRepository: SongRepositoryImpl
) : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>()

    val top40ForYouSongs = songRepository.top40ForYouSongs.asLiveData()
    val songs: LiveData<List<Song>>
        get() = _songs

    fun setSongs(songs: List<Song>) {
        _songs.value = songs
    }

    class Factory @Inject constructor(
        private val songRepository: SongRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ForYouViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ForYouViewModel(songRepository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}