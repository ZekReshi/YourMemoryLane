package at.jku.yourmemorylane.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import at.jku.yourmemorylane.databinding.VisualMediaItemBinding
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory
import com.bumptech.glide.Glide

class VisualMediaAdapter:
    ListAdapter<Media, VisualMediaAdapter.VisualMediaHolder>(DIFF_CALLBACK) {
    private lateinit var onClickListener: OnVisualMediaItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisualMediaHolder {
        val binding = VisualMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VisualMediaHolder(binding)
    }

    override fun onBindViewHolder(holder: VisualMediaHolder, position: Int) {
        val media: Media = getItem(position)
        Glide.with(holder.itemView.context)
            .load(media.path.toUri())
            .into(holder.ivVisualMediaItem)

        with(holder.itemView) {
            tag = media
            setOnClickListener {
                if (this@VisualMediaAdapter::onClickListener.isInitialized) {
                    onClickListener.onItemClick()
                }
            }
        }
    }

    inner class VisualMediaHolder(binding: VisualMediaItemBinding) : ViewHolder(binding.root) {
        var ivVisualMediaItem: ImageView = binding.ivVisualMediaItem
    }

    fun setOnItemClickListener(listener: OnVisualMediaItemClickListener) {
        onClickListener = listener
    }

    interface OnVisualMediaItemClickListener {
        fun onItemClick()
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Media> = object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.type == newItem.type && oldItem.path == newItem.path
            }
        }
    }
}