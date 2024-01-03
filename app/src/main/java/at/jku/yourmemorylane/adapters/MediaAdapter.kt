package at.jku.yourmemorylane.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import at.jku.yourmemorylane.databinding.ImageItemBinding
import at.jku.yourmemorylane.databinding.TextItemBinding
import at.jku.yourmemorylane.databinding.VideoItemBinding
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Type
import com.bumptech.glide.Glide

class MediaAdapter:
    ListAdapter<Media, ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onClickListener: OnItemClickListener

    override fun getItemViewType(position: Int): Int {
        return currentList[position].type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            Type.IMAGE.value -> {
                val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ImageHolder(binding)
            }
            Type.VIDEO.value -> {
                val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return VideoHolder(binding)
            }
            Type.TEXT.value -> {
                val binding = TextItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TextHolder(binding)
            }
        }
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val media: Media = getItem(position)
        when (holder.itemViewType) {
            Type.IMAGE.value -> {
                val imageHolder = holder as ImageHolder
                Glide.with(holder.itemView.context)
                    .load(media.path.toUri())
                    .into(imageHolder.imageView)
            }
            Type.VIDEO.value -> {
                val videoHolder = holder as VideoHolder
                Glide.with(holder.itemView.context)
                    .load(media.path.toUri())
                    .into(videoHolder.imageView)
            }
            Type.TEXT.value -> {
                val textHolder = holder as TextHolder
                textHolder.textView.text = media.path
            }
        }

        with(holder.itemView) {
            tag = media
            setOnClickListener {
                if (this@MediaAdapter::onClickListener.isInitialized && position != RecyclerView.NO_POSITION) {
                    onClickListener.onItemClick(media)
                }
            }
        }
    }

    inner class ImageHolder(binding: ImageItemBinding) : ViewHolder(binding.root) {
        var imageView: ImageView = binding.ivImageItem
    }

    inner class VideoHolder(binding: VideoItemBinding) : ViewHolder(binding.root) {
        var imageView: ImageView = binding.ivVideoItem
    }

    inner class TextHolder(binding: TextItemBinding) : ViewHolder(binding.root) {
        var textView: TextView = binding.tvTextItem
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