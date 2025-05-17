package net.braniumacademy.musicapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.databinding.FragmentHomeBinding
import net.braniumacademy.musicapplication.ui.home.album.AlbumHotViewModel
import net.braniumacademy.musicapplication.ui.searching.SearchingFragmentDirections
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val homeViewModel: HomeViewModel by activityViewModels {
        homeViewModelFactory
    }
    private val albumViewModel: AlbumHotViewModel by activityViewModels()
    private var isObserved = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isObserved) {
            setupObserver()
            isObserved = true
        }
        if (savedInstanceState != null) {
            val scrollPosition = savedInstanceState.getInt(SCROLL_POSITION)
            binding.scrollViewHome.post {
                binding.scrollViewHome.scrollTo(0, scrollPosition)
            }
        }
        binding.btnSearchHome.setOnClickListener {
            val directions = SearchingFragmentDirections.actionGlobalFrSearching()
            NavHostFragment.findNavController(this).navigate(directions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::binding.isInitialized) {
            val scrollPosition = binding.scrollViewHome.scrollY
            outState.putInt(SCROLL_POSITION, scrollPosition)
        }
    }

    private fun setupObserver() {
        homeViewModel.albums.observe(viewLifecycleOwner) {
            albumViewModel.setAlbums(it)
        }
    }

    companion object {
        const val SCROLL_POSITION = "net.braniumacademy.musicapplication.ui.home.SCROLL_POSITION"
    }
}