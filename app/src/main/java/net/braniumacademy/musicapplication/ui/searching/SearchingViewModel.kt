package net.braniumacademy.musicapplication.ui.searching

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedSong
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.history.SearchingRepository
import net.braniumacademy.musicapplication.utils.MusicAppUtils
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@HiltViewModel
class SearchingViewModel @Inject constructor(
    private val repository: SearchingRepository
) : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>()
    private val _searchedKey = MutableLiveData<String>()

    val songs: LiveData<List<Song>>
        get() = _songs
    val keys: LiveData<List<HistorySearchedKey>>
        get() = repository.allKeys.asLiveData()
    val searchedKey: LiveData<String>
        get() = _searchedKey
    val historySearchedSongs: LiveData<List<Song>>
        get() = repository.allSongs.asLiveData()

    fun search(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.search(key.trim())
            result.collectLatest { songList ->
                _songs.postValue(songList)
            }
        }
    }

    fun updatePlaylist(song: Song) {
        val playlistName = MusicAppUtils.DefaultPlaylistName.SEARCH.value
        val playlist = SharedObjectUtils.getPlaylist(playlistName)
        val songs = mutableListOf<Song>()
        playlist?.songs?.let {
            songs.addAll(it)
        }
        if (songs.isEmpty()) {
            songs.add(song)
        } else {
            if(songs.contains(song)) {
                songs.remove(song)
            }
            songs.add(0, song)
        }
        playlist?.updateSongList(songs)
        playlist?.let {
            SharedObjectUtils.addPlaylist(it)
        }
    }

    fun insertSearchedKey(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val keyObj = HistorySearchedKey(key = key.trim())
            repository.insertKey(listOf(keyObj))
        }
    }

    fun insertSearchedSongs(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val historySearchedSong = HistorySearchedSong
                .Builder(song)
                .build()
            val songs = mutableListOf(historySearchedSong)
            repository.insertSong(songs)
        }
    }

    fun setSelectedKey(key: String) {
        _searchedKey.value = key
    }

    fun clearAllKeys() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllKeys()
        }
    }

    fun clearAllSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllSongs()
        }
    }

    class Factory @Inject constructor(
        private val repository: SearchingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchingViewModel::class.java)) {
                return SearchingViewModel(repository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}