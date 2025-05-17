package net.braniumacademy.musicapplication.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.braniumacademy.musicapplication.data.model.song.Song

class DetailViewModel : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>()
    private val _playlistName = MutableLiveData<String>()
    private val _screenName = MutableLiveData<String>()

    val songs: LiveData<List<Song>>
        get() = _songs

    val playlistName: LiveData<String>
        get() = _playlistName

    val screenName: LiveData<String>
        get() = _screenName

    fun setSongs(songs: List<Song>) {
        _songs.value = songs
    }

    fun setPlaylistName(playlistName: String) {
        _playlistName.value = playlistName
    }

    fun setScreenName(screenName: String) {
        _screenName.value = screenName
    }
}