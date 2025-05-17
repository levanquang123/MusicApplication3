package net.braniumacademy.musicapplication.ui.library.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentFavoriteBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.library.LibraryFragmentDirections
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.FAVORITES

@AndroidEntryPoint
class FavoriteFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: SongAdapter
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        adapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    playSong(song, FAVORITES.value)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeFavorite.rvSongList.adapter = adapter
        binding.textTitleFavorite.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.btnMoreFavorite.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = FAVORITES.value
        val screenName = getString(R.string.title_favorite)
        val action = LibraryFragmentDirections.actionLibraryFragmentToDetailFragment(
            screenName,
            playlistName
        )
        findNavController().navigate(action)
    }

    private fun observeData() {
        favoriteViewModel.songs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
            detailViewModel.setSongs(songs)
        }
    }
}