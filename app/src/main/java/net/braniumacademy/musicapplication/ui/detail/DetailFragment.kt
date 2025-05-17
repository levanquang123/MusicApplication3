package net.braniumacademy.musicapplication.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentDetailBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter

@AndroidEntryPoint
class DetailFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var adapter: SongAdapter
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.toolbarDetailSongList.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    val playlistName =
                        DetailFragmentArgs.fromBundle(requireArguments()).playlistName
                    playSong(song, playlistName)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeSongList.rvSongList.adapter = adapter
    }

    private fun observeData() {
        detailViewModel.songs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
        }
        val screenName = DetailFragmentArgs.fromBundle(requireArguments()).screenName
        binding.textTitleDetailSongList.text = screenName
    }
}