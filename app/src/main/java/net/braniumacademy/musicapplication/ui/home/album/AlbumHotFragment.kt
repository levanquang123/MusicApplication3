package net.braniumacademy.musicapplication.ui.home.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.databinding.FragmentAlbumHotBinding
import net.braniumacademy.musicapplication.ui.home.HomeFragmentDirections
import net.braniumacademy.musicapplication.ui.home.HomeViewModel
import net.braniumacademy.musicapplication.ui.home.album.detail.DetailAlbumViewModel
import net.braniumacademy.musicapplication.ui.home.album.more.MoreAlbumViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AlbumHotFragment : Fragment() {
    private lateinit var binding: FragmentAlbumHotBinding
    private lateinit var adapter: AlbumAdapter
    private val albumViewModel: AlbumHotViewModel by activityViewModels()

    @Inject
    lateinit var detailAlbumViewModelFactory: DetailAlbumViewModel.Factory
    private val detailAlbumViewModel: DetailAlbumViewModel by activityViewModels {
        detailAlbumViewModelFactory
    }

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val homeViewModel: HomeViewModel by activityViewModels {
        homeViewModelFactory
    }
    private val moreAlbumViewModel: MoreAlbumViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumHotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.progressBarAlbum.visibility = VISIBLE
        adapter = AlbumAdapter(object : AlbumAdapter.OnAlbumClickListener {
            override fun onAlbumClick(album: Album) {
                detailAlbumViewModel.setAlbum(album)
                navigateToDetailAlbum()
            }
        })
        binding.rvAlbumHot.adapter = adapter
        binding.textTitleAlbumHot.setOnClickListener {
            navigateToMoreAlbum()
        }
        binding.btnMoreAlbumHot.setOnClickListener {
            navigateToMoreAlbum()
        }
    }

    private fun observeData() {
        albumViewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.updateAlbums(albums.sortedBy {
                -it.size
            }.subList(0, 10))
            binding.progressBarAlbum.visibility = GONE
        }
    }

    private fun navigateToDetailAlbum() {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailAlbumFragment()
        findNavController().navigate(action)
    }

    private fun navigateToMoreAlbum() {
        val albums = homeViewModel.albums.value
        moreAlbumViewModel.setAlbums(albums)
        val action = HomeFragmentDirections.actionHomeFragmentToMoreAlbumFragment()
        findNavController().navigate(action)
    }
}