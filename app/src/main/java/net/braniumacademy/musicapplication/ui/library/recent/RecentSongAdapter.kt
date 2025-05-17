package net.braniumacademy.musicapplication.ui.library.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.ItemSongBinding
import net.braniumacademy.musicapplication.utils.MusicAppUtils

class RecentSongAdapter(
    private val songListener: OnSongClickListener,
    private val songOptionMenuListener: OnSongOptionMenuClickListener
) : RecyclerView.Adapter<RecentSongAdapter.ViewHolder>() {
    private val _songs = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(layoutInflater, parent, false)
        val layout = binding.root
        val vto = layout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = layout.measuredWidth
                val height = layout.measuredHeight
                val deltaX = (MusicAppUtils.DEFAULT_MARGIN_END * MusicAppUtils.DENSITY).toInt()
                binding.rootLayoutSongItem.layoutParams.width = width - deltaX
                binding.rootLayoutSongItem.layoutParams.height = height
            }

        })
        return ViewHolder(binding, songListener, songOptionMenuListener)
    }

    override fun getItemCount(): Int {
        return _songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_songs[position], position)
    }

    fun updateSongs(songs: List<Song>) {
        val oldSize = _songs.size
        _songs.clear()
        _songs.addAll(songs)
        if (oldSize > _songs.size) {
            notifyItemRangeRemoved(0, oldSize)
        }
        notifyItemRangeChanged(0, songs.size)
    }

    class ViewHolder(
        private val binding: ItemSongBinding,
        private val songListener: OnSongClickListener,
        private val songOptionMenuListener: OnSongOptionMenuClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, index: Int) {
            binding.textItemSongTitle.text = song.title
            binding.textItemSongArtist.text = song.artist
            Glide.with(binding.root)
                .load(song.image)
                .error(R.drawable.ic_album)
                .into(binding.imageItemSongArtwork)
            binding.root.setOnClickListener {
                songListener.onClick(song, index)
            }
            binding.btnItemSongOption.setOnClickListener {
                songOptionMenuListener.onClick(song)
            }
        }
    }

    interface OnSongClickListener {
        fun onClick(song: Song, index: Int)
    }

    interface OnSongOptionMenuClickListener {
        fun onClick(song: Song)
    }
}