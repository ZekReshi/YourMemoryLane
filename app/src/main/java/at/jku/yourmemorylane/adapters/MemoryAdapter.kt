package at.jku.yourmemorylane.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import at.jku.yourmemorylane.activities.EditActivity
import at.jku.yourmemorylane.databinding.MemoryItemBinding
import at.jku.yourmemorylane.db.entities.Memory

class MemoryAdapter(private val activity: Activity):
    ListAdapter<Memory, MemoryAdapter.MemoryHolder>(DIFF_CALLBACK) {

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
            setOnClickListener { itemView ->
                val item = itemView.tag as Memory
                val intent = Intent(activity, EditActivity::class.java)
                activity.startActivity(intent)
            }
        }
    }

    inner class MemoryHolder(binding: MemoryItemBinding) : ViewHolder(binding.root) {
        var textViewDate: TextView = binding.textViewDate
        var textViewTitle: TextView = binding.textViewTitle
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