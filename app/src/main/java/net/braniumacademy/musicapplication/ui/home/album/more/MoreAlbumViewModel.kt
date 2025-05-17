package net.braniumacademy.musicapplication.ui.home.album.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.braniumacademy.musicapplication.data.model.album.Album

class MoreAlbumViewModel : ViewModel() {
    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    fun setAlbums(albums: List<Album>?) {
        albums?.let { albumList ->
            _albums.value = albumList.sortedBy { -it.size }
        }
    }
}