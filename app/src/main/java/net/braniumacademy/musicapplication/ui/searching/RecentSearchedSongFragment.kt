package net.braniumacademy.musicapplication.ui.searching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentRecentSearchedSongBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.ui.dialog.ConfirmationDialogFragment
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.SEARCH
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class RecentSearchedSongFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentRecentSearchedSongBinding
    private lateinit var songAdapter: SongAdapter

    @Inject
    lateinit var searchingViewModelFactory: SearchingViewModel.Factory
    private val viewModel: SearchingViewModel by activityViewModels {
        searchingViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentSearchedSongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
    }

    private fun setupView() {
        songAdapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    play(song)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.rvHistorySearchedSong.adapter = songAdapter
        binding.tvClearHistorySearchedSong.setOnClickListener {
            val message = R.string.message_confirm_clear_song_history
            val dialog = ConfirmationDialogFragment(
                message,
                object : ConfirmationDialogFragment.OnDeleteConfirmListener {
                    override fun onConfirm(isConfirmed: Boolean) {
                        if (isConfirmed) {
                            viewModel.clearAllSongs()
                        }
                    }
                })
            dialog.show(requireActivity().supportFragmentManager, ConfirmationDialogFragment.TAG)
        }
    }

    private fun play(song: Song) {
        val playlistName = SEARCH.value
        playSong(song, playlistName)
    }

    private fun setupObserver() {
        viewModel.historySearchedSongs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
            SharedObjectUtils.setupPlaylist(songs, SEARCH.value)
        }
    }
}