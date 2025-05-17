package net.braniumacademy.musicapplication.ui.discovery.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.databinding.FragmentArtistBinding
import net.braniumacademy.musicapplication.ui.discovery.DiscoveryFragmentDirections
import net.braniumacademy.musicapplication.ui.discovery.artist.detail.ArtistDetailViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ArtistFragment : Fragment() {
    private lateinit var binding: FragmentArtistBinding
    @Inject
    lateinit var artistViewModelFactory: ArtistViewModel.Factory
    private val artistViewModel: ArtistViewModel by activityViewModels {
        artistViewModelFactory
    }
    @Inject
    lateinit var artistDetailViewModelFactory: ArtistDetailViewModel.Factory
    private val artistDetailViewModel: ArtistDetailViewModel by activityViewModels {
        artistDetailViewModelFactory
    }
    private lateinit var adapter: ArtistPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        adapter = ArtistPagingAdapter(
            object : ArtistPagingAdapter.ArtistListener {
                override fun onClick(artist: Artist) {
                    artistDetailViewModel.setArtist(artist)
                    artistDetailViewModel.getArtistWithSongs(artist.id)
                    navigateToArtistDetail()
                }
            }
        )
        binding.includeArtistList.rvArtist.adapter = adapter
        binding.btnMoreArtist.setOnClickListener {
            navigateToMoreArtist()
        }
        binding.textTitleArtist.setOnClickListener {
            navigateToMoreArtist()
        }
    }

    private fun navigateToArtistDetail() {
        val action = DiscoveryFragmentDirections.actionDiscoveryFragmentToArtistDetailFragment()
        findNavController().navigate(action)
    }

    private fun navigateToMoreArtist() {
        val action = DiscoveryFragmentDirections.actionDiscoveryFragmentToMoreArtistFragment()
        findNavController().navigate(action)
    }

    private fun observeData() {
        lifecycleScope.launch {
            artistViewModel.artistFlow.collectLatest { page ->
                adapter.submitData(page)
            }
        }
    }
}