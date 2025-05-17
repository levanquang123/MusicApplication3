package net.braniumacademy.musicapplication.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.braniumacademy.musicapplication.data.model.PlayingSong
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.recent.RecentSongRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.DEFAULT

object SharedObjectUtils {
    private val _playingSong = PlayingSong()
    private val _playingSongLiveData = MutableLiveData<PlayingSong>()
    private val _currentPlaylist = MutableLiveData<Playlist>()
    private val _playlists: MutableMap<String, Playlist> = HashMap()
    private val _indexToPlay = MutableLiveData<Int>()
    private var _isReady = MutableLiveData<Boolean>()
    private val _currentSongs = mutableListOf<Song>()
    val playingSong: LiveData<PlayingSong> = _playingSongLiveData
    val currentPlaylist: LiveData<Playlist> = _currentPlaylist
    val indexToPlay: LiveData<Int> = _indexToPlay
    val isReady: LiveData<Boolean> = _isReady
    val currentSongs: List<Song> = _currentSongs

    fun initPlaylist() {
        for (playlistName in DefaultPlaylistName.entries.toTypedArray()) {
            val playlist = Playlist(/*_id = -1, */name = playlistName.value)
            playlist.id = -1
            _playlists[playlistName.value] = playlist
        }
    }

    suspend fun updateFavoriteStatus(song: Song, songRepository: SongRepositoryImpl) {
        withContext(Dispatchers.IO) {
            songRepository.updateFavorite(song.id, song.favorite)
        }
    }

    suspend fun insertRecentSongToDB(song: Song, recentSongRepository: RecentSongRepositoryImpl) {
        val recentSong = createRecentSong(song)
        withContext(Dispatchers.IO) {
            recentSongRepository.insert(recentSong)
        }
    }

    suspend fun updateSongInDB(song: Song, songRepository: SongRepositoryImpl) {
        withContext(Dispatchers.IO) {
            ++song.counter
            ++song.replay
            songRepository.update(song)
        }
    }

    fun loadPreviousSessionSong(
        songId: String?,
        playlistName: String?,
        songRepository: SongRepositoryImpl
    ) {
        if (playlistName != null) {
            setCurrentPlaylist(playlistName)
        }
        var playlist = _playlists.getOrDefault(playlistName, null)
        if (playlist == null) {
            playlist = _playlists.getOrDefault(DEFAULT.value, null)
        }
        if (songId != null && playlist != null) {
            _playingSong.playlist = playlist
            var songList = playlist.songs
            if (songList.isEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val song = songRepository.getSongById(songId)
                    song?.let {
                        songList = listOf(it)
                        playlist.updateSongList(songList)
                        if (isBuildInPlaylist(playlistName ?: "")) {
                            postCurrentPlaylist(playlistName!!)
                        } else {
                            playlist.name = DEFAULT.value
                            postCurrentPlaylist(DEFAULT.value)
                        }
                        createLastPlayingSong(songList, songId)
                    }
                }
            } else {
                createLastPlayingSong(songList, songId)
            }
        }
    }

    private fun isBuildInPlaylist(playlistName: String): Boolean {
        for (playlist in DefaultPlaylistName.entries) {
            if (playlistName == playlist.value) {
                return true
            }
        }
        return false
    }

    private fun postCurrentPlaylist(playlistName: String) {
        val playlist = _playlists.getOrDefault(playlistName, null)
        playlist?.let {
            _currentSongs.clear()
            _currentSongs.addAll(it.songs)
            _currentPlaylist.postValue(it)
        }
    }

    private fun createLastPlayingSong(songs: List<Song>, songId: String) {
        val index = songs.indexOfFirst { it.id == songId }
        if (index >= 0) {
            val song = songs[index]
            _playingSong.song = song
            _playingSong.currentIndex = index
            setIndexToPlay(index)
        }
        updatePlayingSong()
    }

    private fun createRecentSong(song: Song): RecentSong {
        return RecentSong.Builder(song).build()
    }

    fun setupPlaylist(songs: List<Song>, playlistName: String) {
        val playlist = _playlists.getOrDefault(playlistName, null)
        playlist?.let {
            it.updateSongList(songs)
            updatePlaylist(it)
            _isReady.value = true
        }
    }

    private fun updatePlaylist(playlist: Playlist) {
        _playlists[playlist.name] = playlist
    }

    fun addPlaylist(playlist: Playlist) {
        updatePlaylist(playlist)
    }

    fun setPlayingSong(index: Int) {
        val currentPlaylistSize = currentSongs.size
        if (index in 0..<currentPlaylistSize) {
            _playingSong.song = currentSongs[index]
            _playingSong.currentIndex = index
            _playingSong.playlist = currentPlaylist.value
            updatePlayingSong()
        }
    }

    private fun updatePlayingSong() {
        _playingSongLiveData.postValue(_playingSong)
    }

    fun setCurrentPlaylist(playlistName: String) {
        val playlist = _playlists.getOrDefault(playlistName, null)
        playlist?.let {
            _currentSongs.clear()
            _currentSongs.addAll(it.songs)
            _currentPlaylist.value = it //.postValue(it)
        }
    }

    fun setIndexToPlay(index: Int) {
        _indexToPlay.postValue(index)
    }

    fun getPlaylist(playlistName: String): Playlist? {
        return _playlists.getOrDefault(playlistName, null)
    }
}