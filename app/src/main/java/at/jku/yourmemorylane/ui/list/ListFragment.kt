package at.jku.yourmemorylane.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.activities.EditActivity
import at.jku.yourmemorylane.activities.EditActivity.Companion.EXTRA_DATE
import at.jku.yourmemorylane.activities.EditActivity.Companion.EXTRA_ID
import at.jku.yourmemorylane.activities.EditActivity.Companion.EXTRA_TITLE
import at.jku.yourmemorylane.adapters.MemoryAdapter
import at.jku.yourmemorylane.databinding.FragmentListBinding
import at.jku.yourmemorylane.db.entities.Memory
import javax.security.auth.callback.Callback


class ListFragment : Fragment() {

    private lateinit var editActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var listViewModel: ListViewModel
    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val intent = result.data!!
            val id = intent.getIntExtra(EXTRA_ID, -1)
            if (id > 0) {
                val title = intent.getStringExtra(EXTRA_TITLE)!!
                val date = intent.getStringExtra(EXTRA_DATE)!!
                val memory = Memory(title, date, 0.0, 0.0)
                listViewModel.update(memory)
            } else {
                val title = intent.getStringExtra(EXTRA_TITLE)!!
                val date = intent.getStringExtra(EXTRA_DATE)!!
                val memory = Memory(title, date, 0.0, 0.0)
                listViewModel.insert(memory)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.memoryRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        val memoryAdapter = MemoryAdapter()
        recyclerView.adapter = memoryAdapter
        memoryAdapter.setOnItemClickListener(object : MemoryAdapter.OnItemClickListener {
            override fun onItemClick(memory: Memory) {
                val intent = Intent(activity, EditActivity::class.java)
                intent.putExtra(EXTRA_ID, memory.id)
                intent.putExtra(EXTRA_TITLE, memory.title)
                intent.putExtra(EXTRA_DATE, memory.date)

                editActivityLauncher.launch(intent)
            }
        })

        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        listViewModel.getMemories().observe(viewLifecycleOwner) { memories -> memoryAdapter.submitList(memories) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}