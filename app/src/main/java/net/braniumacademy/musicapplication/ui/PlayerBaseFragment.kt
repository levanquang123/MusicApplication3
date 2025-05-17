package net.braniumacademy.musicapplication.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import net.braniumacademy.musicapplication.NavigationNowPlayingDirections
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.ui.dialog.SongOptionMenuDialogFragment
import net.braniumacademy.musicapplication.ui.dialog.SongOptionMenuViewModel
import net.braniumacademy.musicapplication.utils.MusicAppUtils
import net.braniumacademy.musicapplication.utils.PermissionUtils
import net.braniumacademy.musicapplication.utils.SharedObjectUtils

open class PlayerBaseFragment : Fragment() {
    protected fun playSong(song: Song, playlistName: String) {
        val isPermissionGranted = PermissionUtils.permissionGranted.value
        if (isPermissionGranted != null && isPermissionGranted) {
            doNavigate(song, playlistName)
        } else if (!PermissionUtils.isRegistered) {
            PermissionUtils
                .permissionGranted
                .observe(requireActivity()) {
                    if (it) {
                        doNavigate(song, playlistName)
                    }
                }
            PermissionUtils.isRegistered = true
        }
    }

    private fun doNavigate(song: Song, playlistName: String) {
        SharedObjectUtils.setCurrentPlaylist(playlistName)
        val songs = SharedObjectUtils.currentSongs
        val songIndex = MusicAppUtils.getSongIndex(song, songs)
        SharedObjectUtils.setIndexToPlay(songIndex)
        val action = NavigationNowPlayingDirections.actionGlobalNavigationNowPlaying()
        findNavController().navigate(action)
    }

    protected fun showOptionMenu(song: Song) {
        val menuDialogFragment = SongOptionMenuDialogFragment.newInstance
        val menuDialogViewMode: SongOptionMenuViewModel by activityViewModels()
        menuDialogViewMode.setSong(song)
        menuDialogFragment.show(
            requireActivity().supportFragmentManager,
            SongOptionMenuDialogFragment.TAG
        )
    }
}