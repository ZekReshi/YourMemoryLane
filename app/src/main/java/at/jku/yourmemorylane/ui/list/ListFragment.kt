package at.jku.yourmemorylane.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.adapters.MemoryAdapter
import at.jku.yourmemorylane.databinding.FragmentListBinding
import at.jku.yourmemorylane.db.entities.Memory
import javax.security.auth.callback.Callback


class ListFragment : Fragment() {

    private lateinit var listViewModel: ListViewModel
    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        val memoryAdapter = MemoryAdapter(requireActivity())
        recyclerView.adapter = memoryAdapter



        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        listViewModel.getMemories().observe(viewLifecycleOwner) { memories -> memoryAdapter.submitList(memories) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}