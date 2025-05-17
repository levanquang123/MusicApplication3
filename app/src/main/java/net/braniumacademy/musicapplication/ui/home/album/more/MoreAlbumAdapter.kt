package net.braniumacademy.musicapplication.ui.home.album.more

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.databinding.ItemMoreAlbumBinding
import net.braniumacademy.musicapplication.ui.home.album.AlbumAdapter

class MoreAlbumAdapter(
    private val listener: AlbumAdapter.OnAlbumClickListener
) : RecyclerView.Adapter<MoreAlbumAdapter.ViewHolder>() {
    private val albums = mutableListOf<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMoreAlbumBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    fun updateAlbums(newAlbums: List<Album>?) {
        newAlbums?.let {
            val oldSize = albums.size
            albums.clear()
            albums.addAll(it)
            if (oldSize > albums.size) {
                notifyItemRangeRemoved(0, oldSize)
            }
            notifyItemRangeChanged(0, albums.size)
        }
    }

    class ViewHolder(
        private val binding: ItemMoreAlbumBinding,
        private val listener: AlbumAdapter.OnAlbumClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            binding.textNameItemMoreAlbum.text = album.name
            Glide.with(itemView.context)
                .load(album.artwork)
                .error(R.drawable.ic_album)
                .into(binding.imageItemMoreAlbum)
            binding.root.setOnClickListener {
                listener.onAlbumClick(album)
            }
        }
    }
}