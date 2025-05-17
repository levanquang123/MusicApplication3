package net.braniumacademy.musicapplication.ui.home.recommended

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentRecommendedBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongPagingAdapter
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.home.HomeFragmentDirections
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.RECOMMENDED
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class RecommendedFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentRecommendedBinding

    @Inject
    lateinit var recommendedViewModelFactory: RecommendedViewModel.Factory
    private val recommendedViewModel: RecommendedViewModel by activityViewModels {
        recommendedViewModelFactory
    }
    private lateinit var adapter: SongPagingAdapter
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecommendedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        adapter = SongPagingAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    val playlistName = RECOMMENDED.value
                    playSong(song, playlistName)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeSongList.rvSongList.adapter = adapter
        binding.btnMoreRecommended.setOnClickListener {
            navigateToMoreRecommended()
        }
        binding.textTitleRecommended.setOnClickListener {
            navigateToMoreRecommended()
        }
        lifecycleScope.launch {
            recommendedViewModel.songFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun setupViewModel() {
        recommendedViewModel.songs.observe(viewLifecycleOwner) { songs ->
            detailViewModel.setSongs(songs)
            val playlistName = RECOMMENDED.value
            SharedObjectUtils.setupPlaylist(songs, playlistName)
        }
    }

    private fun navigateToMoreRecommended() {
        val action = HomeFragmentDirections.actionHomeFragmentToMoreRecommendedFragment2()
        findNavController().navigate(action)
    }
}