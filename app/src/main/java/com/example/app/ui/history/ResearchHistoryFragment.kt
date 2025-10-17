package com.example.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.databinding.FragmentResearchHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResearchHistoryFragment : Fragment() {
    
    private var _binding: FragmentResearchHistoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ResearchHistoryViewModel by viewModels()
    private lateinit var adapter: ResearchHistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResearchHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeHistory()
    }
    
    private fun setupRecyclerView() {
        adapter = ResearchHistoryAdapter { document ->
            // Handle document click - show details or export
            // You can implement this based on your needs
        }
        
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ResearchHistoryFragment.adapter
        }
    }
    
    private fun observeHistory() {
        lifecycleScope.launch {
            viewModel.researchHistory.collect { documents ->
                adapter.submitList(documents)
                binding.emptyState.visibility = if (documents.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
