package net.braniumacademy.musicapplication.ui.discovery.artist.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentArtistDetailBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class ArtistDetailFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentArtistDetailBinding
    private lateinit var adapter: SongAdapter
    @Inject
    lateinit var artistDetailViewModelFactory: ArtistDetailViewModel.Factory
    private val artistDetailViewModel: ArtistDetailViewModel by activityViewModels {
        artistDetailViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.toolbarArtistDetail.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    prepare(song, index)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeDetailArtistSongList.rvSongList.adapter = adapter
    }

    private fun prepare(song: Song, index: Int) {
        val artist = artistDetailViewModel.artist.value
        val playlistName = artist?.name ?: ""
        val playlist = Playlist(name = playlistName)
        playlist.id = -1
        val songs = artistDetailViewModel.artistWithSongs.value?.songs
        songs?.let {
            playlist.updateSongList(it)
            SharedObjectUtils.addPlaylist(playlist)
            playSong(song, playlistName)
        }
    }

    private fun observeData() {
        artistDetailViewModel.artistWithSongs.observe(viewLifecycleOwner) { artistWithSongs ->
            adapter.updateSongs(artistWithSongs.songs)
        }
        artistDetailViewModel.artist.observe(viewLifecycleOwner) { artist ->
            showArtistInfo(artist)
        }
    }

    private fun showArtistInfo(artist: Artist) {
        binding.textDetailArtistName.text = getString(R.string.text_artist_name, artist.name)
        binding.textDetailInterested.text =
            getString(R.string.text_number_subscriber, artist.interested)
        val interested = if (artist.isCareAbout) "YES" else "NO"
        binding.textArtistDetailYourInterest.text =
            getString(R.string.text_your_interested, interested)
        Glide.with(binding.root)
            .load(artist.avatar)
            .error(R.drawable.ic_artist)
            .circleCrop()
            .into(binding.imageArtistDetailAvatar)
    }
}