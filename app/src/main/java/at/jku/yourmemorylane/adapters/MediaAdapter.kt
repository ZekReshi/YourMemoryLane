package at.jku.yourmemorylane.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.jku.yourmemorylane.databinding.MediaItemBinding
import at.jku.yourmemorylane.db.entities.Media
import com.bumptech.glide.Glide

class MediaAdapter:
    ListAdapter<Media, MediaAdapter.MediaHolder>(DIFF_CALLBACK) {
    private lateinit var onClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaHolder {
        val binding = MediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaHolder, position: Int) {
        val media: Media = getItem(position)
        Glide.with(holder.imageView.context)
            .load(media.path.toUri())
            .into(holder.imageView)

        with(holder.itemView) {
            tag = media
            setOnClickListener {
                if (this@MediaAdapter::onClickListener.isInitialized && position != RecyclerView.NO_POSITION) {
                    onClickListener.onItemClick(media)
                }
            }
        }
    }

    inner class MediaHolder(binding: MediaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var imageView: ImageView = binding.imageView
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(media: Media)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Media> = object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.path == newItem.path
            }
        }
    }
}