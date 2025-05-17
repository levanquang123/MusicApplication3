package net.braniumacademy.musicapplication.playback

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.ExecutionException

class MusicPlaybackService : Service() {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var _mediaController: MediaController? = null
    private lateinit var localBinder: LocalBinder
    private val _isMediaControllerInitialized = MutableLiveData<Boolean>()

    override fun onCreate() {
        super.onCreate()
        localBinder = LocalBinder()
        createMediaPlayer()
    }

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun createMediaPlayer() {
        val sessionToken = SessionToken(
            applicationContext,
            ComponentName(applicationContext, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(applicationContext, sessionToken).buildAsync()
        controllerFuture.addListener({
            if (controllerFuture.isDone && !controllerFuture.isCancelled) {
                try {
                    _mediaController = controllerFuture.get()
                    _mediaController?.let {
                        _isMediaControllerInitialized.postValue(true)
                    }
                } catch (ignored: ExecutionException) {
                } catch (ignored: InterruptedException) {
                }
            } else {
                _mediaController = null
            }
        }, MoreExecutors.directExecutor())
    }

    fun playPause() {
        _mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun setRepeatMode() {
        _mediaController?.let {
            val repeatMode = it.repeatMode
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> it.repeatMode = Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> it.repeatMode = Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> it.repeatMode = Player.REPEAT_MODE_OFF
            }
        }
    }

    fun setShuffleMode() {
        _mediaController?.let {
            val isShuffle = it.shuffleModeEnabled
            it.shuffleModeEnabled = !isShuffle
        }
    }

    inner class LocalBinder : Binder() {
        val service: MusicPlaybackService
            get() = this@MusicPlaybackService

        val mediaController: MediaController?
            get() = _mediaController

        val isMediaControllerInitialized: LiveData<Boolean>
            get() = _isMediaControllerInitialized
    }
}