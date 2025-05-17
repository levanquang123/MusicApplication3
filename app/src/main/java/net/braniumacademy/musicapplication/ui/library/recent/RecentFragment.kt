package net.braniumacademy.musicapplication.ui.library.recent

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentRecentBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.library.LibraryFragmentDirections
import net.braniumacademy.musicapplication.utils.MusicAppUtils
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.RECENT

@AndroidEntryPoint
class RecentFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentRecentBinding
    private lateinit var adapter: RecentSongAdapter
    private val recentViewModel: RecentViewModel by activityViewModels()
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        adapter = RecentSongAdapter(
            object : RecentSongAdapter.OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    playSong(song, RECENT.value)
                }
            },
            object : RecentSongAdapter.OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        val layoutManager = MyLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.HORIZONTAL,
            false
        )
        binding.rvRecent.adapter = adapter
        binding.rvRecent.layoutManager = layoutManager
        binding.progressRecentHeard.visibility = View.VISIBLE
        binding.textTitleRecent.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.btnMoreRecent.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = RECENT.value
        val screenName = getString(R.string.title_recent)
        val action = LibraryFragmentDirections.actionLibraryFragmentToDetailFragment(
            screenName,
            playlistName
        )
        findNavController().navigate(action)
    }

    private fun observeData() {
        recentViewModel.recentSongs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
            detailViewModel.setSongs(songs)
            binding.progressRecentHeard.visibility = View.GONE
        }
    }

    internal class MyLayoutManager(
        context: Context,
        spanCount: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {
        override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
            val deltaX = (MusicAppUtils.DEFAULT_MARGIN_END * MusicAppUtils.DENSITY).toInt()
            lp.width = width - deltaX
            return true
        }
    }
}