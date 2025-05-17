package net.braniumacademy.musicapplication.ui.discovery.mostheard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentMostHeardBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.discovery.DiscoveryFragmentDirections
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.MOST_HEARD
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class MostHeardFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentMostHeardBinding
    private lateinit var songAdapter: SongAdapter

    @Inject
    lateinit var mostHeardViewModelFactory: MostHeardViewModel.Factory
    private val mostHeardViewModel: MostHeardViewModel by activityViewModels {
        mostHeardViewModelFactory
    }
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMostHeardBinding.inflate(inflater, container, false)
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
                    val playlistName = MOST_HEARD.value
                    playSong(song, playlistName)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeMostHeardSong.rvSongList.adapter = songAdapter
        binding.btnMoreMostHeard.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.textTitleMostHeard.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = MOST_HEARD.value
        val screenName = getString(R.string.title_most_heard)
        val action = DiscoveryFragmentDirections.actionDiscoveryFragmentToDetailFragment(
            screenName,
            playlistName
        )
        findNavController().navigate(action)
    }

    private fun observeData() {
        mostHeardViewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
        }
        mostHeardViewModel.top40MostHeardSongs.observe(viewLifecycleOwner) { songs ->
            detailViewModel.setSongs(songs)
            SharedObjectUtils.setupPlaylist(songs, MOST_HEARD.value)
        }
    }
}