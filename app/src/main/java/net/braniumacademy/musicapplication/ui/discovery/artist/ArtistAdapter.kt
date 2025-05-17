package net.braniumacademy.musicapplication.ui.discovery.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.databinding.ItemArtistBinding

class ArtistAdapter(
    private val listener: ArtistListener
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {
    private val _artists = mutableListOf<Artist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemArtistBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun getItemCount(): Int {
        return _artists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_artists[position])
    }

    fun updateArtists(artists: List<Artist>) {
        val oldSize = _artists.size
        _artists.clear()
        _artists.addAll(artists)
        if (oldSize > _artists.size) {
            notifyItemRangeRemoved(0, oldSize)
        }
        notifyItemRangeChanged(0, _artists.size)
    }

    class ViewHolder(
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
}