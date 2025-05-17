package net.braniumacademy.musicapplication.ui.home.album.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class DetailAlbumViewModel @Inject constructor(
    private val songRepository: SongRepositoryImpl
) : ViewModel() {
    private val _album = MutableLiveData<Album>()
    private val _songs = MutableLiveData<List<Song>>()
    private val _playlist = MutableLiveData<Playlist>()

    val album: LiveData<Album>
        get() = _album
    val songs: LiveData<List<Song>>
        get() = _songs
    val playlist: LiveData<Playlist>
        get() = _playlist

    fun setAlbum(album: Album) {
        _album.value = album
    }

    fun loadSongs(album: Album) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPlaylist = Playlist(name = album.name)
            currentPlaylist.id = -1
            val key = "%${album.name}%"
            val songsList = songRepository.getSongsInAlbum(key)
            _songs.postValue(songsList)
            currentPlaylist.updateSongList(songsList)
            _playlist.postValue(currentPlaylist)
        }
    }

    class Factory @Inject constructor(
        private val songRepository: SongRepositoryImpl
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailAlbumViewModel::class.java)) {
                return DetailAlbumViewModel(songRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}