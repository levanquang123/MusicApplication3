package net.braniumacademy.musicapplication.ui.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.braniumacademy.musicapplication.data.model.song.Song

class DialogSongInfoViewModel : ViewModel() {
    private val _song = MutableLiveData<Song>()

    val song: MutableLiveData<Song>
        get() = _song

    fun setSong(song: Song) {
        _song.value = song
    }
}