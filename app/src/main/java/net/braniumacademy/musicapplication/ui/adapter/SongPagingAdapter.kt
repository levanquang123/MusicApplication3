package net.braniumacademy.musicapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.ItemSongBinding
import net.braniumacademy.musicapplication.utils.PermissionUtils

class SongPagingAdapter(
    private val onSongClickListener: OnSongClickListener,
    private val onSongOptionMenuClickListener: OnSongOptionMenuClickListener
) : PagingDataAdapter<Song, SongPagingAdapter.SongViewHolder>(SONG_COMPARATOR) {
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        if (song != null) {
            holder.bind(song, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(layoutInflater, parent, false)
        return SongViewHolder(binding, onSongClickListener, onSongOptionMenuClickListener)
    }

    class SongViewHolder(
        private val binding: ItemSongBinding,
        private val onSongClickListener: OnSongClickListener,
        private val onSongOptionMenuClickListener: OnSongOptionMenuClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, index: Int) {
            binding.textItemSongTitle.text = song.title
            binding.textItemSongArtist.text = song.artist
            Glide.with(binding.root)
                .load(song.image)
                .error(R.drawable.ic_album)
                .into(binding.imageItemSongArtwork)
            binding.root.setOnClickListener {
                val isGranted = PermissionUtils.permissionGranted.value
                if (isGranted == null || !isGranted) {
                    PermissionUtils.askPermission()
                }
                onSongClickListener.onClick(song, index)
            }
            binding.btnItemSongOption.setOnClickListener {
                onSongOptionMenuClickListener.onClick(song)
            }
        }
    }

    companion object {
        private val SONG_COMPARATOR = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }
        }
    }
}