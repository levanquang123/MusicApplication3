package net.braniumacademy.musicapplication.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.databinding.FragmentDiscoveryBinding
import net.braniumacademy.musicapplication.ui.discovery.artist.ArtistViewModel
import net.braniumacademy.musicapplication.ui.discovery.foryou.ForYouViewModel
import net.braniumacademy.musicapplication.ui.discovery.mostheard.MostHeardViewModel
import net.braniumacademy.musicapplication.ui.searching.SearchingFragmentDirections
import javax.inject.Inject

@AndroidEntryPoint
class DiscoveryFragment : Fragment() {
    private lateinit var binding: FragmentDiscoveryBinding
    @Inject
    lateinit var artistViewModelFactory: ArtistViewModel.Factory
    private val artistViewModel: ArtistViewModel by activityViewModels {
        artistViewModelFactory
    }
    @Inject
    lateinit var mostHeardViewModelFactory: MostHeardViewModel.Factory
    private val mostHeardViewModel: MostHeardViewModel by activityViewModels {
        mostHeardViewModelFactory
    }
    @Inject
    lateinit var discoveryViewModelFactory: DiscoveryViewModel.Factory
    private val discoveryViewModel: DiscoveryViewModel by activityViewModels {
        discoveryViewModelFactory
    }
    @Inject
    lateinit var forYouViewModelFactory: ForYouViewModel.Factory
    private val forYouViewModel: ForYouViewModel by activityViewModels {
        forYouViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        if (savedInstanceState != null) {
            binding.scrollViewDiscovery.post {
                binding.scrollViewDiscovery.scrollY = savedInstanceState.getInt(SCROLL_POSITION)
            }
        }
        binding.btnSearchDiscovery.setOnClickListener {
            val directions = SearchingFragmentDirections.actionGlobalFrSearching()
            NavHostFragment.findNavController(this).navigate(directions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POSITION, binding.scrollViewDiscovery.scrollY)
    }

    private fun observeData() {
        discoveryViewModel.localArtists.observe(viewLifecycleOwner) { artists ->
            discoveryViewModel.saveArtistSongCrossRef(artists)
        }
        discoveryViewModel.top15Artists.observe(viewLifecycleOwner) { artists ->
            artistViewModel.setArtists(artists)
        }
        discoveryViewModel.top15MostHeardSongs.observe(viewLifecycleOwner) { songs ->
            mostHeardViewModel.setSongs(songs)
        }
        discoveryViewModel.top15ForYouSongs.observe(viewLifecycleOwner) { songs ->
            forYouViewModel.setSongs(songs)
        }
    }

    companion object {
        const val SCROLL_POSITION =
            "net.braniumacademy.musicapplication.ui.discovery.SCROLL_POSITION"
    }
}