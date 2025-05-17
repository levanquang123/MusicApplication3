package net.braniumacademy.musicapplication.ui.playing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.session.MediaController
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class NowPlayingViewModel @Inject constructor() : ViewModel() {
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    fun setIsPlaying(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun getDuration(duration: Long): Int {
        if (duration == C.TIME_UNSET) {
            return 300 * 1000
        }
        return duration.toInt()
    }

    fun getTimeLabel(value: Long): String {
        val minute = value / 60000
        val second = (value / 1000) % 60
        return if (value < 0 || value > Int.MAX_VALUE) "00:00"
        else String.format(Locale.ENGLISH, "%02d:%02d", minute, second)
    }
}