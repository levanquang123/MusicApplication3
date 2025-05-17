package net.braniumacademy.musicapplication.ui.playing

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.databinding.ActivityNowPlayingBinding
import net.braniumacademy.musicapplication.playback.MusicPlaybackService
import net.braniumacademy.musicapplication.utils.MusicAppUtils
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityNowPlayingBinding
    private var mediaController: MediaController? = null
    private lateinit var pressedAnimator: Animator
    private lateinit var seekBarHandler: Handler
    private lateinit var seekbarCallback: Runnable
    private lateinit var rotationAnimator: ObjectAnimator
    private var currentFraction: Float = 0f
    private val nowPlayingViewModel: NowPlayingViewModel by viewModels()

    private var musicService: MusicPlaybackService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            val binder = service as MusicPlaybackService.LocalBinder
            musicService = binder.service
            binder.isMediaControllerInitialized.observe(this@NowPlayingActivity) {
                if (it) {
                    mediaController = binder.mediaController
                    setupMediaController()
                    mediaController?.let { controller ->
                        if (!controller.isPlaying) {
                            controller.play()
                        }
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    @Inject
    lateinit var songRepository: SongRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNowPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        readIncomingIntent()
        setupViewModel()
        setupAnimator()
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(applicationContext, MusicPlaybackService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            // nếu hệ điều hành Android hiện tại là Android 14+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE, R.anim.fade_in, R.anim.slide_down
                )
            } else { // các hệ điều hành cũ hơn:
                @Suppress("DEPRECATION")
                overridePendingTransition(R.anim.fade_in, R.anim.slide_down)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::seekBarHandler.isInitialized) {
            seekBarHandler.removeCallbacks(seekbarCallback)
        }
    }

    override fun onClick(v: View) {
        pressedAnimator.setTarget(v)
        pressedAnimator.start()
        when (v) {
            binding.btnPlayPauseNowPlaying -> setupPlayPauseAction()
            binding.btnShuffle -> setupShuffleAction()
            binding.btnSkipPrevNowPlaying -> setupSkipPrevious()
            binding.btnSkipNextNowPlaying -> setupSkipNext()
            binding.btnRepeat -> setupRepeatAction()
            binding.btnShareNowPlaying -> {}
            binding.btnFavoriteNowPlaying -> setupFavoriteAction()
            else -> {}
        }
    }

    private fun setupPlayPauseAction() {
        musicService?.playPause()
    }

    private fun setupShuffleAction() {
        musicService?.setShuffleMode()
        showShuffleState()
    }

    private fun setupSkipPrevious() {
        if (mediaController?.hasPreviousMediaItem() == true) {
            mediaController?.seekToPreviousMediaItem()
            rotationAnimator.end()
        }
    }

    private fun setupSkipNext() {
        if (mediaController?.hasNextMediaItem() == true) {
            mediaController?.seekToNextMediaItem()
            rotationAnimator.end()
        }
    }

    private fun setupRepeatAction() {
        musicService?.setRepeatMode()
        showRepeatMode()
    }

    private fun setupFavoriteAction() {
        val playingSong = SharedObjectUtils.playingSong.value
        playingSong?.let {
            val song = it.song
            if (song != null) {
                song.favorite = !song.favorite
                showFavoriteState(song)
                lifecycleScope.launch {
                    SharedObjectUtils.updateFavoriteStatus(song, songRepository)
                }
            }
        }
    }

    private fun setupView() {
        binding.btnPlayPauseNowPlaying.setOnClickListener(this)
        binding.btnShuffle.setOnClickListener(this)
        binding.btnSkipPrevNowPlaying.setOnClickListener(this)
        binding.btnSkipNextNowPlaying.setOnClickListener(this)
        binding.btnRepeat.setOnClickListener(this)
        binding.btnShareNowPlaying.setOnClickListener(this)
        binding.btnFavoriteNowPlaying.setOnClickListener(this)
        binding.toolbarNowPlaying.setNavigationOnClickListener {
            rotationAnimator.pause()
            val intent = Intent().apply {
                rotationAnimator.pause()
                putExtra(MusicAppUtils.EXTRA_CURRENT_FRACTION, rotationAnimator.animatedFraction)
            }
            setResult(RESULT_OK, intent)
            onBackPressedDispatcher.onBackPressed()
        }
        binding.seekBarNowPlaying.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaController?.seekTo(progress.toLong())
                }
                binding.textLabelCurrentDuration.text =
                    nowPlayingViewModel.getTimeLabel(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun readIncomingIntent() {
        currentFraction = intent.getFloatExtra(MusicAppUtils.EXTRA_CURRENT_FRACTION, 0f)
    }

    private fun setupViewModel() {
        nowPlayingViewModel.isPlaying.observe(this) { isPlaying ->
            val iconId = if (isPlaying) {
                R.drawable.ic_pause_circle
            } else {
                R.drawable.ic_play_circle
            }
            binding.btnPlayPauseNowPlaying.setImageResource(iconId)
            if (isPlaying) {
                if (rotationAnimator.isPaused) {
                    rotationAnimator.resume()
                } else {
                    rotationAnimator.start()
                }
            } else {
                rotationAnimator.pause()
            }
        }
    }

    private fun setupSeekBar() {
        seekBarHandler = Looper.myLooper()?.let { Handler(it) }!!
        seekbarCallback = object : Runnable {
            override fun run() {
                if (mediaController != null) {
                    val currentPosition = mediaController?.currentPosition?.toInt() ?: 0
                    binding.seekBarNowPlaying.progress = currentPosition
                }
                seekBarHandler.postDelayed(this, 1000)
            }
        }
        seekBarHandler.post(seekbarCallback)
    }

    private fun showSongInfo(song: Song?) {
        if (song != null) {
            updateSeekBarMaxValue()
            updateDuration()
            binding.textAlbumNowPlaying.text = song.album
            binding.textSongTitleNowPlaying.text = song.title
            binding.textSongArtistNowPlaying.text = song.artist
            Glide.with(this)
                .load(song.image)
                .error(R.drawable.ic_album)
                .circleCrop()
                .into(binding.imageArtworkNowPlaying)
            showRepeatMode()
            showShuffleState()
            showFavoriteState(song)
        }
    }

    private fun updateSeekBarMaxValue() {
        val currentPos = mediaController?.currentPosition?.toInt() ?: 0
        binding.seekBarNowPlaying.progress = currentPos
        val duration = mediaController?.duration ?: 0
        binding.seekBarNowPlaying.max = nowPlayingViewModel.getDuration(duration)
    }

    private fun updateDuration() {
        val duration = mediaController?.duration ?: 0
        val durationLabel = nowPlayingViewModel.getTimeLabel(duration)
        binding.textTotalDuration.text = durationLabel
    }

    private fun showRepeatMode() {
        val repeatMode = mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF
        val iconId = when (repeatMode) {
            Player.REPEAT_MODE_OFF -> R.drawable.ic_repeat_off
            Player.REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
            Player.REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
            else -> R.drawable.ic_repeat_off
        }
        binding.btnRepeat.setImageResource(iconId)
    }

    private fun showShuffleState() {
        val isShuffle = mediaController?.shuffleModeEnabled ?: false
        val iconId = if (isShuffle) {
            R.drawable.ic_shuffle_on
        } else {
            R.drawable.ic_shuffle
        }
        binding.btnShuffle.setImageResource(iconId)
    }

    private fun showFavoriteState(song: Song) {
        val favoriteIcon = if (song.favorite) {
            R.drawable.ic_favorite_on
        } else {
            R.drawable.ic_favorite_off
        }
        binding.btnFavoriteNowPlaying.setImageResource(favoriteIcon)
    }

    private fun setupMediaListener() {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                nowPlayingViewModel.setIsPlaying(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaController?.isPlaying == true) {
                    updateSeekBarMaxValue()
                    updateDuration()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    updateSeekBarMaxValue()
                    updateDuration()
                }
            }
        }
        mediaController?.addListener(listener)
    }

    private fun setupAnimator() {
        pressedAnimator = AnimatorInflater.loadAnimator(this, R.animator.button_pressed)
        rotationAnimator = ObjectAnimator
            .ofFloat(binding.imageArtworkNowPlaying, "rotation", 0f, 360f)
        rotationAnimator.interpolator = LinearInterpolator()
        rotationAnimator.duration = 12000
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.RESTART
        rotationAnimator.setCurrentFraction(currentFraction)
    }

    private fun setupMediaController() {
        SharedObjectUtils.playingSong.observe(this) { playingSong ->
            nowPlayingViewModel.setIsPlaying(mediaController?.isPlaying == true)
            setupSeekBar()
            showSongInfo(playingSong.song)
        }
        setupMediaListener()
    }
}