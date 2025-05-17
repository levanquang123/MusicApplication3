package net.braniumacademy.musicapplication.ui.home.album.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.databinding.FragmentMoreAlbumBinding
import net.braniumacademy.musicapplication.ui.home.album.AlbumAdapter
import net.braniumacademy.musicapplication.ui.home.album.detail.DetailAlbumViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MoreAlbumFragment : Fragment() {
    private lateinit var binding: FragmentMoreAlbumBinding
    private lateinit var adapter: MoreAlbumAdapter
    private val moreAlbumViewModel: MoreAlbumViewModel by activityViewModels()

    @Inject
    lateinit var detailAlbumViewModelFactory: DetailAlbumViewModel.Factory
    private val detailAlbumViewModel: DetailAlbumViewModel by activityViewModels {
        detailAlbumViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        binding.toolbarMoreAlbum.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        adapter = MoreAlbumAdapter(object : AlbumAdapter.OnAlbumClickListener {
            override fun onAlbumClick(album: Album) {
                detailAlbumViewModel.setAlbum(album)
                navigateToDetailAlbum()
            }
        })
        binding.rvMoreAlbum.adapter = adapter
    }

    private fun setupViewModel() {
        moreAlbumViewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.updateAlbums(albums)
        }
    }

    private fun navigateToDetailAlbum() {
        val action = MoreAlbumFragmentDirections.actionMoreAlbumFragmentToDetailAlbumFragment()
        findNavController().navigate(action)
    }
}