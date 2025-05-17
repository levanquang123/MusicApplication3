package net.braniumacademy.musicapplication.ui.playing

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.databinding.FragmentMiniPlayerBinding
import net.braniumacademy.musicapplication.playback.MusicPlaybackService
import net.braniumacademy.musicapplication.utils.MusicAppUtils
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.SEARCH
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class MiniPlayerFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentMiniPlayerBinding

    @Inject
    lateinit var songRepository: SongRepositoryImpl

    @Inject
    lateinit var viewModelFactory: MiniPlayerViewModel.Factory
    private val viewModel: MiniPlayerViewModel by activityViewModels {
        viewModelFactory
    }
    private var mediaController: MediaController? = null
    private lateinit var pressedAnimator: Animator
    private lateinit var rotationAnimator: ObjectAnimator
    private var currentFraction: Float = 0f
    private val nowPlayingActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            currentFraction = result.data
                ?.getFloatExtra(MusicAppUtils.EXTRA_CURRENT_FRACTION, 0f) ?: 0f
            if (mediaController?.isPlaying == true) {
                rotationAnimator.start()
                rotationAnimator.setCurrentFraction(currentFraction)
                currentFraction = 0f
            } else {
                rotationAnimator.setCurrentFraction(currentFraction)
            }
        }
    }
    private var musicService: MusicPlaybackService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlaybackService.LocalBinder
            musicService = binder.service
            binder.isMediaControllerInitialized.observe(this@MiniPlayerFragment) {
                if (it) {
                    if (mediaController == null) {
                        mediaController = binder.mediaController
                        setupListener()
                        setupObserveForMediaController()
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().bindService(
            Intent(requireActivity().applicationContext, MusicPlaybackService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAnimator()
        setupObserve()
    }

    override fun onResume() {
        super.onResume()
        val currentPlayingSong = SharedObjectUtils.playingSong.value?.song
        currentPlayingSong?.let {
            val songId = it.id
            viewModel.getCurrentPlayingSong(songId)
            viewModel.currentPlayingSong.observe(viewLifecycleOwner) { song ->
                if (song != null) {
                    updateFavoriteStatus(song)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(serviceConnection)
    }

    override fun onClick(v: View) {
        pressedAnimator.setTarget(v)
        pressedAnimator.start()
        when (v) {
            binding.btnMiniPlayerPlayPause -> musicService?.playPause()

            binding.btnMiniPlayerSkipNext ->
                if (mediaController?.hasNextMediaItem() == true) {
                    mediaController?.seekToNextMediaItem()
                    rotationAnimator.end()
                }

            binding.btnMiniPlayerFavorite -> setupFavorite()
        }
    }

    private fun setupFavorite() {
        val playingSong = SharedObjectUtils.playingSong.value
        playingSong?.let {
            val song = it.song
            song!!.favorite = !song.favorite
            updateFavoriteStatus(song)
            lifecycleScope.launch {
                SharedObjectUtils.updateFavoriteStatus(song, songRepository)
            }
        }
    }

    private fun setupView() {
        binding.btnMiniPlayerFavorite.setOnClickListener(this)
        binding.btnMiniPlayerPlayPause.setOnClickListener(this)
        binding.btnMiniPlayerSkipNext.setOnClickListener(this)
        binding.textMiniPlayerTitle.maxLines = 1
        binding.textMiniPlayerTitle.ellipsize = TextUtils.TruncateAt.END
        binding.root.setOnClickListener {
            navigateToNowPlaying()
        }
    }

    private fun setupAnimator() {
        pressedAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.button_pressed)
        rotationAnimator = ObjectAnimator
            .ofFloat(binding.imageMiniPlayerArtwork, "rotation", 0f, 360f)
        rotationAnimator.interpolator = LinearInterpolator()
        rotationAnimator.duration = 12000
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.RESTART
    }

    private fun setupListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                viewModel.setPlayingState(isPlaying)
            }
        })
    }

    private fun setupObserveForMediaController() {
        mediaController?.let {
            viewModel.mediaItems.observe(viewLifecycleOwner) { mediaItems ->
                if (!MusicAppUtils.isConfigChanged) {
                    it.setMediaItems(mediaItems)
                }
            }
            SharedObjectUtils.indexToPlay.observe(viewLifecycleOwner) { index ->
                if (!MusicAppUtils.isConfigChanged) {
                    val playingSong = SharedObjectUtils.playingSong.value
                    var currentPlaylist: Playlist? = null
                    if (playingSong != null) {
                        currentPlaylist = playingSong.playlist
                    }
                    val playlistToPlay = SharedObjectUtils.currentPlaylist.value
                    // TH1: cùng playlist, cùng index -> KHÔNG phát lại bài hát mà tiếp tục
                    // TH2: khác playlist, cùng index -> phát bài hát từ đầu
                    val condition1 = it.mediaItemCount > index && it.currentMediaItemIndex != index
                    val condition2 = playlistToPlay != null && it.currentMediaItemIndex == index
                            && playlistToPlay.id != currentPlaylist?.id
                    val condition3 = index == 0 && it.mediaItemCount == 1 && !it.isPlaying
                    val condition4 = it.currentMediaItemIndex == index
                            && playlistToPlay?.name == SEARCH.value

                    if (index > -1 && (condition1 || condition2 || condition3 || condition4)) {
                        it.seekTo(index, 0)
                        it.prepare()
                    }
                } else {
                    MusicAppUtils.isConfigChanged = false
                }
            }
        }
    }

    private fun setupObserve() {
        SharedObjectUtils.playingSong.observe(viewLifecycleOwner) {
            it.song?.let { song ->
                showSongInfo(song)
            }
        }
        SharedObjectUtils.currentPlaylist.observe(viewLifecycleOwner) { playlist ->
            val playingSong = SharedObjectUtils.playingSong.value
            var currentPlaylist: Playlist? = null
            if (playingSong != null) {
                currentPlaylist = playingSong.playlist
            }
            val haveMoreSongToPlay = (mediaController != null
                    && playlist.songs.size > mediaController!!.mediaItemCount)
            if (playlist.mediaItems.isNotEmpty()
                && (currentPlaylist == null
                        || playlist.name == SEARCH.value
                        || currentPlaylist.id != playlist.id || haveMoreSongToPlay)
            ) {
                viewModel.setMediaItem(playlist.mediaItems)
            }
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) { // playing
                binding.btnMiniPlayerPlayPause.setImageResource(R.drawable.ic_pause_circle)
                if (rotationAnimator.isPaused) {
                    if (currentFraction != 0f) {
                        rotationAnimator.start()
                        rotationAnimator.setCurrentFraction(currentFraction)
                        currentFraction = 0f
                    } else {
                        rotationAnimator.resume()
                    }
                } else if (!rotationAnimator.isRunning) {
                    rotationAnimator.start()
                }
            } else {
                binding.btnMiniPlayerPlayPause.setImageResource(R.drawable.ic_play_circle)
                rotationAnimator.pause()
            }
        }
    }

    private fun showSongInfo(song: Song) {
        binding.textMiniPlayerTitle.text = song.title
        binding.textMiniPlayerArtist.text = song.artist
        Glide.with(requireContext())
            .load(song.image)
            .error(R.drawable.ic_album)
            .circleCrop()
            .into(binding.imageMiniPlayerArtwork)
        updateFavoriteStatus(song)
    }

    private fun updateFavoriteStatus(song: Song) {
        val favoriteIcon = if (song.favorite) {
            R.drawable.ic_favorite_on
        } else {
            R.drawable.ic_favorite_off
        }
        binding.btnMiniPlayerFavorite.setImageResource(favoriteIcon)
    }

    private fun navigateToNowPlaying() {
        val intent = Intent(requireContext(), NowPlayingActivity::class.java).apply {
            rotationAnimator.pause()
            putExtra(MusicAppUtils.EXTRA_CURRENT_FRACTION, rotationAnimator.animatedFraction)
        }
        val options = ActivityOptionsCompat
            .makeCustomAnimation(requireContext(), R.anim.slide_up, R.anim.fade_out)
        nowPlayingActivityLauncher.launch(intent, options)
    }
}