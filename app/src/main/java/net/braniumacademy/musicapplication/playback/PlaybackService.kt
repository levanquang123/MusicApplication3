package net.braniumacademy.musicapplication.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.recent.RecentSongRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.ui.playing.NowPlayingActivity
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    private lateinit var mediaSession: MediaSession
    private lateinit var listener: Player.Listener

    private val singleTopActivity: PendingIntent
        get() {
            val intent = Intent(applicationContext, NowPlayingActivity::class.java)
            return PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    @Inject
    lateinit var recentSongRepository: RecentSongRepositoryImpl

    @Inject
    lateinit var songRepository: SongRepositoryImpl

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onCreate() {
        super.onCreate()
        initSessionAndPlayer()
        setupListener()
    }

    override fun onDestroy() {
        if (this::mediaSession.isInitialized) {
            mediaSession.player.removeListener(listener)
            mediaSession.player.release()
            mediaSession.release()
        }
        super.onDestroy()
    }

    private fun initSessionAndPlayer() {
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()
        val builder = MediaSession.Builder(this, player)
        val intent = singleTopActivity
        builder.setSessionActivity(intent)
        mediaSession = builder.build()
    }

    private fun setupListener() {
        val player = mediaSession.player
        listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val playlistChanged =
                    reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED
                val indexToPlay = SharedObjectUtils.indexToPlay.value ?: 0
                if (!playlistChanged || indexToPlay == 0) {
                    SharedObjectUtils.setPlayingSong(player.currentMediaItemIndex)
                    saveDataToDB()
                }
            }
        }
        player.addListener(listener)
    }

    private fun saveDataToDB() {
        val song = extractSong()
        if (song != null) {
            val handler = Looper.myLooper()?.let {
                Handler(it)
            }
            handler?.postDelayed({
                val player = mediaSession.player
                if (player.isPlaying) {
                    val coroutineScope = CoroutineScope(Dispatchers.IO)
                    coroutineScope.launch {
                        SharedObjectUtils.insertRecentSongToDB(song, recentSongRepository)
                        saveCounterAndReplay()
                    }
                }
            }, 5000)
        }
    }

    private fun saveCounterAndReplay() {
        val song = extractSong()
        song?.let {
            val handlerThread = HandlerThread(
                "UpdateCounterAndReplayThread",
                Process.THREAD_PRIORITY_BACKGROUND
            )
            handlerThread.start()
            val handler = Handler(handlerThread.looper)
            handler.post {
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch {
                    SharedObjectUtils.updateSongInDB(song, songRepository)
                }
            }
        }
    }

    private fun extractSong(): Song? {
        return SharedObjectUtils.playingSong.value?.song
    }
}