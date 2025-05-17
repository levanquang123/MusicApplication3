package net.braniumacademy.musicapplication.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.ui.library.playlist.PlaylistAdapter
import net.braniumacademy.musicapplication.ui.library.playlist.PlaylistViewModel

class DialogAddSongToPlaylistFragment(
    private val listener: OnPlaylistSelectedListener
) : DialogFragment() {
    private lateinit var adapter: PlaylistAdapter
    private val viewModel: PlaylistViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setupComponents()
        val builder = AlertDialog.Builder(requireActivity())
        val layoutInflater = requireActivity().layoutInflater
        val rootView = layoutInflater.inflate(R.layout.fragment_dialog_add_song_to_playlist, null)
        val recycleViewPlaylist = rootView.findViewById<RecyclerView>(R.id.rv_dialog_playlist)
        recycleViewPlaylist.adapter = adapter
        val btnCancel = rootView.findViewById<MaterialButton>(R.id.btn_cancel)
        val btnCreate = rootView.findViewById<MaterialButton>(R.id.btn_create)
        val editPlaylistName = rootView.findViewById<TextInputEditText>(R.id.edit_playlist_name)
        btnCreate.setOnClickListener {
            createPlaylist(editPlaylistName)
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        builder.setView(rootView)
        loadPlaylistList()
        observePlaylistName(editPlaylistName)
        viewModel.findPlaylistByName("")
        return builder.create()
    }

    private fun setupComponents() {
        adapter = PlaylistAdapter(object : PlaylistAdapter.OnPlaylistClickListener {
            override fun onPlaylistClick(playlist: Playlist) {
                listener.onPlaylistSelected(playlist)
                dismiss()
            }

            override fun onPlaylistMenuOptionClick(playlist: Playlist) {
                // todo
            }
        })
    }

    private fun createPlaylist(editPlaylistName: TextInputEditText) {
        if (editPlaylistName.text != null) {
            val newPlaylistName = editPlaylistName.text.toString().trim()
            if (newPlaylistName.isEmpty()) {
                editPlaylistName.error = getString(R.string.error_empty_playlist_name)
            }
            if (editPlaylistName.error == null) {
                viewModel.createNewPlaylist(newPlaylistName)
                editPlaylistName.text!!.clear()
                closeKeyboard(editPlaylistName)
            }
        }
    }

    private fun closeKeyboard(editPlaylistName: TextInputEditText) {
        val inputMethodManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editPlaylistName.windowToken, 0)
    }

    private fun loadPlaylistList() {
        viewModel.playlists.observe(requireActivity()) { playlists ->
            adapter.updatePlaylists(playlists)
        }
    }

    private fun observePlaylistName(editPlaylistName: TextInputEditText) {
        editPlaylistName.doOnTextChanged { text, _, _, _ ->
            text?.let {
                viewModel.findPlaylistByName(it.toString())
            }
        }
        viewModel.playlist.observe(requireActivity()) { playlist ->
            if (playlist == null) {
                editPlaylistName.error = null
            } else {
                editPlaylistName.error = getString(R.string.error_playlist_exists)
            }
        }
    }

    interface OnPlaylistSelectedListener {
        fun onPlaylistSelected(playlist: Playlist)
    }

    companion object {
        const val TAG = "DialogAddSongToPlaylistFragment"
    }
}