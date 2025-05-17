package net.braniumacademy.musicapplication.ui.discovery.foryou

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentForYouBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.discovery.DiscoveryFragmentDirections
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.FOR_YOU
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class ForYouFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentForYouBinding
    private lateinit var songAdapter: SongAdapter

    @Inject
    lateinit var forYouViewModelFactory: ForYouViewModel.Factory
    private val forYouViewModel: ForYouViewModel by activityViewModels {
        forYouViewModelFactory
    }
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForYouBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        songAdapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    val playlistName = FOR_YOU.value
                    playSong(song, playlistName)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeForYouSong.rvSongList.adapter = songAdapter
        binding.btnMoreForYou.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.textTitleForYou.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = FOR_YOU.value
        val screenName = getString(R.string.title_for_you)
        val action = DiscoveryFragmentDirections.actionDiscoveryFragmentToDetailFragment(
            screenName,
            playlistName
        )
        findNavController().navigate(action)
    }

    private fun observeData() {
        forYouViewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
        }
        forYouViewModel.top40ForYouSongs.observe(viewLifecycleOwner) { songs ->
            detailViewModel.setSongs(songs)
            SharedObjectUtils.setupPlaylist(songs, FOR_YOU.value)
        }
    }
}