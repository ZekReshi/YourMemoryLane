package at.jku.yourmemorylane.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import at.jku.yourmemorylane.databinding.MemoryItemBinding
import at.jku.yourmemorylane.db.entities.Memory

class MemoryAdapter:
    ListAdapter<Memory, MemoryAdapter.MemoryHolder>(DIFF_CALLBACK) {
    private lateinit var onClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryHolder {
        val binding = MemoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoryHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoryHolder, position: Int) {
        val memory: Memory = getItem(position)
        holder.textViewDate.text = memory.date
        holder.textViewTitle.text = memory.title

        with(holder.itemView) {
            tag = memory
            setOnClickListener {
                if (this@MemoryAdapter::onClickListener.isInitialized && position != NO_POSITION) {
                    onClickListener.onItemClick(memory)
                }
            }
        }
    }

    inner class MemoryHolder(binding: MemoryItemBinding) : ViewHolder(binding.root) {
        var textViewDate: TextView = binding.textViewDate
        var textViewTitle: TextView = binding.textViewTitle
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(memory: Memory)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Memory> = object : DiffUtil.ItemCallback<Memory>() {
            override fun areItemsTheSame(oldItem: Memory, newItem: Memory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Memory, newItem: Memory): Boolean {
                return oldItem.title == newItem.title && oldItem.date == newItem.date
            }
        }
    }
}