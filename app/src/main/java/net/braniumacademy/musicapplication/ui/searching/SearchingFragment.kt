package net.braniumacademy.musicapplication.ui.searching

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentSearchingBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.adapter.OnSongClickListener
import net.braniumacademy.musicapplication.ui.adapter.OnSongOptionMenuClickListener
import net.braniumacademy.musicapplication.ui.adapter.SongAdapter
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.SEARCH
import javax.inject.Inject

@AndroidEntryPoint
class SearchingFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentSearchingBinding
    private lateinit var adapter: SongAdapter

    @Inject
    lateinit var searchingViewModelFactory: SearchingViewModel.Factory
    private val viewModel: SearchingViewModel by activityViewModels {
        searchingViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupSearchView()
        setupObserver()
    }

    private fun setupView() {
        binding.toolbarSearching.setNavigationOnClickListener {
            viewModel.setSelectedKey("")
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        adapter = SongAdapter(
            object : OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    viewModel.insertSearchedSongs(song)
                    play(song)
                }
            },
            object : OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeSearchedResult.rvSearchedSong.adapter = adapter
    }

    private fun play(song: Song) {
        viewModel.updatePlaylist(song)
        val playlistName = SEARCH.value
        playSong(song, playlistName)
    }

    private fun setupSearchView() {
        val manager: SearchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchViewHome
            .setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))
        binding.searchViewHome.isIconifiedByDefault = false
        binding.searchViewHome.isSubmitButtonEnabled = true
        binding.searchViewHome.isQueryRefinementEnabled = true
        binding.searchViewHome.onActionViewExpanded()
        binding.searchViewHome.maxWidth = Int.MAX_VALUE
        binding.searchViewHome.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                activeSearchResultLayout(true)
                if (query.trim().isNotEmpty()) {
                    performSearch(query)
                    viewModel.insertSearchedKey(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.trim().isNotEmpty()) {
                    activeSearchResultLayout(true)
                    performSearch(newText.trim())
                } else {
                    activeSearchResultLayout(false)
                }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        viewModel.search(query)
    }

    private fun activeSearchResultLayout(shouldShowSearchResult: Boolean) {
        val searchResultVisibility = if (shouldShowSearchResult) View.VISIBLE else View.GONE
        val historyVisibility = if (shouldShowSearchResult) View.GONE else View.VISIBLE
        binding.includeSearchedResult.rvSearchedSong.visibility = searchResultVisibility
        binding.includeSearchedHistory.fcvHistorySearchedKey.visibility = historyVisibility
        binding.includeSearchedHistory.fcvHistorySearchedSong.visibility = historyVisibility
    }

    private fun setupObserver() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
        }
        viewModel.searchedKey.observe(viewLifecycleOwner) { key ->
            binding.searchViewHome.setQuery(key, false)
        }
    }
}