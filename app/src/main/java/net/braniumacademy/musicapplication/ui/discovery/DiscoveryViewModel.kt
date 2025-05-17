package net.braniumacademy.musicapplication.ui.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import javax.inject.Inject

class DiscoveryViewModel @Inject constructor(
    private val artistRepository: ArtistRepositoryImpl,
    private val songRepository: SongRepositoryImpl
) : ViewModel() {
    val localArtists = artistRepository.artists.asLiveData()
    val top15Artists = artistRepository.top15Artists.asLiveData()
    val top15MostHeardSongs = songRepository.top15MostHeardSongs.asLiveData()
    val top15ForYouSongs = songRepository.top15ForYouSongs.asLiveData()

    fun saveArtistSongCrossRef(artists: List<Artist>) {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.songs.collectLatest { localSongs ->
                val crossRefs = mutableListOf<ArtistSongCrossRef>()
                if (artists.isNotEmpty()) {
                    for (artist in artists) {
                        for (song in localSongs) {
                            val key = ".*${artist.name.lowercase()}.*"
                            if (song.artist.lowercase().matches(key.toRegex())) {
                                crossRefs.add(ArtistSongCrossRef(song.id, artist.id))
                            }
                        }
                    }
                }
                val crossRefToInsert = crossRefs.toTypedArray()
                artistRepository.insertArtistSongCrossRef(*crossRefToInsert)
            }
        }
    }

    class Factory @Inject constructor(
        private val artistRepository: ArtistRepositoryImpl,
        private val songRepository: SongRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiscoveryViewModel(artistRepository, songRepository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}