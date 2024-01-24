package at.jku.yourmemorylane.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.jku.yourmemorylane.activities.EditActivity
import at.jku.yourmemorylane.activities.EditActivity.Companion.EXTRA_ID
import at.jku.yourmemorylane.adapters.MemoryAdapter
import at.jku.yourmemorylane.databinding.FragmentListBinding
import at.jku.yourmemorylane.db.entities.Memory
import at.jku.yourmemorylane.viewmodels.ListViewModel


class ListFragment : Fragment() {

    private lateinit var editActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var listViewModel: ListViewModel
    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
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

        val memoryAdapter = MemoryAdapter(this)
        recyclerView.adapter = memoryAdapter
        memoryAdapter.setOnItemClickListener(object : MemoryAdapter.OnMemoryClickListener {
            override fun onItemClick(memory: Memory) {
                val intent = Intent(activity, EditActivity::class.java)
                intent.putExtra(EXTRA_ID, memory.id)

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