package net.braniumacademy.musicapplication.ui.library.playlist.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentPlaylistDetailBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.utils.MusicAppUtils

@AndroidEntryPoint
class PlaylistDetailFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentPlaylistDetailBinding
    private lateinit var adapter: SongAdapter
    private val playlistDetailViewModel: PlaylistDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.includePlaylistDetail
            .toolbarPlaylistDetail
            .setNavigationOnClickListener {
                requireActivity().supportFragmentManager
                    .popBackStack()
            }
        val title = getString(R.string.title_playlist_detail)
        binding.includePlaylistDetail
            .textPlaylistDetailToolbarTitle.text = title
        adapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    val playlistName =
                        playlistDetailViewModel.playlistWithSongs.value?.playlist?.name
                            ?: MusicAppUtils.DefaultPlaylistName.DEFAULT.value
                    playSong(song, playlistName)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includePlaylistDetail
            .includeSongList
            .rvSongList.adapter = adapter
    }

    private fun observeData() {
        playlistDetailViewModel.playlistWithSongs
            .observe(viewLifecycleOwner) { playlistWithSongs ->
                adapter.updateSongs(playlistWithSongs.songs)
                binding.includePlaylistDetail
                    .textPlaylistDetailTitle.text = playlistWithSongs.playlist?.name
                val numberOfSong =
                    getString(R.string.text_number_of_songs, playlistWithSongs.songs.size)
                binding.includePlaylistDetail
                    .textPlaylistDetailNumOfSong.text = numberOfSong
                val artworkId = playlistWithSongs.songs.firstOrNull()?.image
                Glide.with(this)
                    .load(artworkId)
                    .error(R.drawable.ic_album)
                    .into(binding.includePlaylistDetail.imagePlaylistArtwork)
            }
    }
}