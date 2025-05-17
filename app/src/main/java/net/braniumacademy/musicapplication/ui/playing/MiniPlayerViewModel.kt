package net.braniumacademy.musicapplication.ui.playing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class MiniPlayerViewModel @Inject constructor(
    private val songRepository: SongRepositoryImpl
) : ViewModel() {
    private val _mediaItems = MutableLiveData<List<MediaItem>>()
    private val _isPlaying = MutableLiveData<Boolean>()
    private val _currentPlayingSong = MutableLiveData<Song?>()
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems
    val isPlaying: LiveData<Boolean> = _isPlaying
    val currentPlayingSong: LiveData<Song?> = _currentPlayingSong

    fun setMediaItem(mediaItems: List<MediaItem>) {
        _mediaItems.value = mediaItems
    }

    fun setPlayingState(state: Boolean) {
        _isPlaying.value = state
    }

    fun getCurrentPlayingSong(songId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = songRepository.getSongById(songId)
            _currentPlayingSong.postValue(result)
        }
    }

    class Factory @Inject constructor(
        private val songRepository: SongRepositoryImpl
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MiniPlayerViewModel::class.java)) {
                return MiniPlayerViewModel(songRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}