package net.braniumacademy.musicapplication.ui.home.recommended.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentMoreRecommendedBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongPagingAdapter
import net.braniumacademy.musicapplication.ui.home.HomeViewModel
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.RECOMMENDED
import javax.inject.Inject

@AndroidEntryPoint
class MoreRecommendedFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentMoreRecommendedBinding
    private lateinit var adapter: SongPagingAdapter

    @Inject
    lateinit var moreRecommendedViewModelFactory: MoreRecommendedViewModel.Factory
    private val moreRecommendedViewModel: MoreRecommendedViewModel by activityViewModels {
        moreRecommendedViewModelFactory
    }

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val homeViewModel: HomeViewModel by activityViewModels {
        homeViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreRecommendedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
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
        binding.includeMoreRecommended.rvSongList.adapter = adapter
        binding.toolbarMoreRecommended.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            moreRecommendedViewModel.songFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        homeViewModel.localSongs.observe(viewLifecycleOwner) { songs ->
            val playlistName = RECOMMENDED.value
            SharedObjectUtils.setupPlaylist(songs, playlistName)
        }
    }
}