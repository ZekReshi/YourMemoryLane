package at.jku.yourmemorylane.adapters

import android.media.browse.MediaBrowser.ItemCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.db.entities.Memory

class MemoryAdapter:
    ListAdapter<Memory, MemoryAdapter.MemoryHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.memory_item, parent, false)
        return MemoryHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoryHolder, position: Int) {
        val currentMemory: Memory = getItem(position)
        holder.textViewDate.text = currentMemory.date
        holder.textViewTitle.text = currentMemory.title
    }

    class MemoryHolder(itemView: View) : ViewHolder(itemView) {
        var textViewDate: TextView
        var textViewTitle: TextView

        init {
            textViewDate = itemView.findViewById(R.id.text_view_date)
            textViewTitle = itemView.findViewById(R.id.text_view_title)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Memory> = object : DiffUtil.ItemCallback<Memory>() {
            override fun areItemsTheSame(oldItem: Memory, newItem: Memory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Memory, newItem: Memory): Boolean {
                return oldItem.title == newItem.title && oldItem.date == newItem.date;
            }
        }
    }

}