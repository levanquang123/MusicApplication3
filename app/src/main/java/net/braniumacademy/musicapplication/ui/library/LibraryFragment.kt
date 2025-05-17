package net.braniumacademy.musicapplication.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.databinding.FragmentLibraryBinding
import net.braniumacademy.musicapplication.ui.library.favorite.FavoriteViewModel
import net.braniumacademy.musicapplication.ui.library.recent.RecentViewModel
import net.braniumacademy.musicapplication.ui.searching.SearchingFragmentDirections
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.FAVORITES
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.RECENT
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding

    @Inject
    lateinit var libraryViewModelFactory: LibraryViewModel.Factory
    private val libraryViewModel: LibraryViewModel by viewModels {
        libraryViewModelFactory
    }
    private val recentSongViewModel: RecentViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        if (savedInstanceState != null) {
            val scrollPosition = savedInstanceState.getInt(SCROLL_POSITION)
            binding.scrollViewLibrary.post {
                binding.scrollViewLibrary.scrollTo(0, scrollPosition)
            }
        }
        binding.btnSearchLibrary.setOnClickListener {
            val directions = SearchingFragmentDirections.actionGlobalFrSearching()
            NavHostFragment.findNavController(this).navigate(directions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POSITION, binding.scrollViewLibrary.scrollY)
    }

    private fun observeData() {
        val sharedObjectUtils = SharedObjectUtils
        libraryViewModel.recentSongs.observe(viewLifecycleOwner) { recentSongs ->
            recentSongViewModel.setRecentSongs(recentSongs)
            sharedObjectUtils.setupPlaylist(recentSongs, RECENT.value)
        }
        libraryViewModel.favoriteSongs.observe(viewLifecycleOwner) { favoriteSongs ->
            favoriteViewModel.setSongs(favoriteSongs)
            sharedObjectUtils.setupPlaylist(favoriteSongs, FAVORITES.value)
        }
    }

    companion object {
        const val SCROLL_POSITION = "net.braniumacademy.musicapplication.ui.library.SCROLL_POSITION"
    }
}