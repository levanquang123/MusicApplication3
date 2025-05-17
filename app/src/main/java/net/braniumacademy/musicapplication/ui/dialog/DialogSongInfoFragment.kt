package net.braniumacademy.musicapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentDialogSongInfoBinding

class DialogSongInfoFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDialogSongInfoBinding
    private val viewModel: DialogSongInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogSongInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.song.observe(viewLifecycleOwner) { song ->
            showSongInfo(song)
        }
    }

    private fun showSongInfo(song: Song) {
        val title = getString(R.string.text_song_detail_title, song.title)
        val artist = getString(R.string.text_song_detail_artist, song.artist)
        val album = getString(R.string.text_song_detail_album, song.album)
        val duration = getString(R.string.text_song_detail_duration, song.duration)
        val counter = getString(R.string.text_song_detail_counter, song.counter)
        val replay = getString(R.string.text_song_detail_replay_counter, song.replay)
        val favorite = getString(
            R.string.text_song_detail_favorite_status,
            if (song.favorite) getString(R.string.yes) else getString(R.string.no)
        )
        val notAvailable = getString(R.string.not_available)
        val genre = getString(R.string.text_song_detail_genre, notAvailable)
        val year = getString(R.string.text_song_detail_year, notAvailable)

        Glide.with(this)
            .load(song.image)
            .error(R.drawable.ic_album)
            .circleCrop()
            .into(binding.imageDetailSongArtwork)
        binding.textSongDetailTitle.text = title
        binding.textSongDetailArtist.text = artist
        binding.textSongDetailAlbum.text = album
        binding.textSongDetailDuration.text = duration
        binding.textSongDetailCounter.text = counter
        binding.textSongDetailReplayCounter.text = replay
        binding.textSongDetailFavoriteStatus.text = favorite
        binding.textSongDetailGenre.text = genre
        binding.textSongDetailYear.text = year
    }

    companion object {
        fun newInstance() = DialogSongInfoFragment()
        const val TAG = "DialogSongInfoFragment"
    }
}