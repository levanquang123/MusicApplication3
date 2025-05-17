package net.braniumacademy.musicapplication.ui.discovery.artist.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.databinding.FragmentMoreArtistBinding
import net.braniumacademy.musicapplication.ui.discovery.artist.ArtistPagingAdapter
import net.braniumacademy.musicapplication.ui.discovery.artist.detail.ArtistDetailViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MoreArtistFragment : Fragment() {
    private lateinit var binding: FragmentMoreArtistBinding
    private lateinit var adapter: ArtistPagingAdapter
    @Inject
    lateinit var moreArtistViewModelFactory: MoreArtistViewModel.Factory
    private val moreArtistViewModel: MoreArtistViewModel by viewModels {
        moreArtistViewModelFactory
    }
    @Inject
    lateinit var artistDetailViewModelFactory: ArtistDetailViewModel.Factory
    private val artistDetailViewModel: ArtistDetailViewModel by activityViewModels {
        artistDetailViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.toolbarMoreArtist.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = ArtistPagingAdapter(
            object : ArtistPagingAdapter.ArtistListener {
                override fun onClick(artist: Artist) {
                    artistDetailViewModel.setArtist(artist)
                    artistDetailViewModel.getArtistWithSongs(artist.id)
                    navigateToArtistDetail()
                }
            }
        )
        binding.includeArtist.rvArtist.adapter = adapter
    }

    private fun navigateToArtistDetail() {
        val action = MoreArtistFragmentDirections.actionMoreArtistFragmentToArtistDetailFragment()
        findNavController().navigate(action)
    }

    private fun observeData() {
        lifecycleScope.launch {
            moreArtistViewModel.artistFlow.collectLatest { page ->
                adapter.submitData(page)
            }
        }
    }
}