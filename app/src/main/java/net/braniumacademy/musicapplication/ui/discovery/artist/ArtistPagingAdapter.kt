package net.braniumacademy.musicapplication.ui.discovery.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.databinding.ItemArtistBinding

class ArtistPagingAdapter(
    private val listener: ArtistListener
) : PagingDataAdapter<Artist, ArtistPagingAdapter.ArtistViewHolder>(ARTIST_COMPARATOR) {
    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = getItem(position)
        if (artist != null) {
            holder.bind(artist)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemArtistBinding.inflate(layoutInflater, parent, false)
        return ArtistViewHolder(binding, listener)
    }

    class ArtistViewHolder(
        private val binding: ItemArtistBinding,
        private val listener: ArtistListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(artist: Artist) {
            binding.textArtistName.text = artist.name
            val subscribeData =
                binding.root.context.getString(R.string.text_number_subscriber, artist.interested)
            binding.textArtistSubscribe.text = subscribeData
            Glide.with(binding.root)
                .load(artist.avatar)
                .error(R.drawable.ic_artist)
                .circleCrop()
                .into(binding.imageArtistAvatar)
            binding.root.setOnClickListener {
                listener.onClick(artist)
            }
        }
    }

    interface ArtistListener {
        fun onClick(artist: Artist)
    }

    companion object {
        private val ARTIST_COMPARATOR = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }
        }
    }
}