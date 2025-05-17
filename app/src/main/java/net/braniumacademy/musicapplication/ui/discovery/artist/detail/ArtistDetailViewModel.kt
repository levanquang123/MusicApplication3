package net.braniumacademy.musicapplication.ui.discovery.artist.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistWithSongs
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import javax.inject.Inject

class ArtistDetailViewModel @Inject constructor(
    private val artistRepository: ArtistRepositoryImpl
) : ViewModel() {
    private val _artist = MutableLiveData<Artist>()
    private val _artistWithSongs = MutableLiveData<ArtistWithSongs>()

    val artist: LiveData<Artist> = _artist
    val artistWithSongs: LiveData<ArtistWithSongs> = _artistWithSongs

    fun setArtist(artist: Artist) {
        _artist.value = artist
    }

    fun getArtistWithSongs(artistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = artistRepository.getArtistWithSongs(artistId)
            _artistWithSongs.postValue(result)
        }
    }

    class Factory @Inject constructor(
        private val artistRepository: ArtistRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArtistDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ArtistDetailViewModel(artistRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}