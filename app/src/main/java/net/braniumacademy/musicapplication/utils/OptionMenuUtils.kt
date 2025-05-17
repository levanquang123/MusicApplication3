package net.braniumacademy.musicapplication.utils

import android.content.res.Configuration
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.ui.dialog.MenuItem

object OptionMenuUtils {
    private val menuItems = mutableListOf<MenuItem>()

    init {
        createSongOptionMenuItems()
    }

    private fun createSongOptionMenuItems() {
        menuItems.add(MenuItem(OptionMenu.DOWNLOAD, R.drawable.ic_download, R.string.item_download))
        menuItems.add(
            MenuItem(
                OptionMenu.ADD_TO_FAVOURITES,
                R.drawable.ic_favorite,
                R.string.item_add_to_favourites
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.ADD_TO_PLAYLIST,
                R.drawable.ic_playlist,
                R.string.item_add_to_playlist
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.ADD_TO_QUEUE,
                R.drawable.ic_queue,
                R.string.item_add_to_queue
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.VIEW_ALBUM,
                R.drawable.ic_album_black,
                R.string.item_view_album
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.VIEW_ARTIST,
                R.drawable.ic_singer,
                R.string.item_view_artist
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.BLOCK,
                R.drawable.ic_block,
                R.string.item_block
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.REPORT_ERROR,
                R.drawable.ic_report,
                R.string.item_report_error
            )
        )
        menuItems.add(
            MenuItem(
                OptionMenu.VIEW_SONG_INFORMATION,
                R.drawable.ic_information,
                R.string.item_view_song_information
            )
        )
    }

    @JvmStatic
    val songOptionMenuItems: List<MenuItem>
        get() = menuItems

    enum class OptionMenu(val value: String) {
        DOWNLOAD("download"),
        ADD_TO_FAVOURITES("add_favorite"),
        ADD_TO_PLAYLIST("add_playlist"),
        ADD_TO_QUEUE("add_queue"),
        VIEW_ALBUM("view_album"),
        VIEW_ARTIST("view_artist"),
        BLOCK("block"),
        REPORT_ERROR("report_error"),
        VIEW_SONG_INFORMATION("view_song_information")
    }
}