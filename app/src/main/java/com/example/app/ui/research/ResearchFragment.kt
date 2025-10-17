package com.example.app.ui.research

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.app.databinding.FragmentResearchBinding
import com.example.app.domain.PDFExportManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResearchFragment : Fragment() {
    
    private var _binding: FragmentResearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ResearchViewModel by viewModels()
    
    @Inject
    lateinit var pdfExportManager: PDFExportManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeState()
    }
    
    private fun setupUI() {
        binding.apply {
            researchButton.setOnClickListener {
                val prompt = researchPromptEditText.text.toString().trim()
                if (prompt.isNotEmpty()) {
                    viewModel.startResearch(prompt)
                } else {
                    Toast.makeText(requireContext(), "Please enter a research prompt", Toast.LENGTH_SHORT).show()
                }
            }
            
            historyButton.setOnClickListener {
                findNavController().navigate(
                    ResearchFragmentDirections.actionResearchFragmentToResearchHistoryFragment()
                )
            }
            
            clearButton.setOnClickListener {
                researchPromptEditText.text.clear()
                viewModel.clearState()
            }
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            viewModel.researchState.collect { state ->
                when (state) {
                    is com.example.app.data.model.ResearchState.Idle -> {
                        showLoading(false)
                        binding.researchResultText.text = "Enter a research prompt to begin..."
                    }
                    is com.example.app.data.model.ResearchState.Loading -> {
                        showLoading(true)
                        binding.researchResultText.text = "Conducting research... This may take a minute."
                    }
                    is com.example.app.data.model.ResearchState.Success -> {
                        showLoading(false)
                        displayResearchResults(state.document)
                        showExportButton(state.document)
                    }
                    is com.example.app.data.model.ResearchState.Error -> {
                        showLoading(false)
                        binding.researchResultText.text = "Error: ${state.message}"
                        Toast.makeText(requireContext(), "Research failed: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun displayResearchResults(document: com.example.app.data.model.ResearchDocument) {
        val resultText = buildString {
            append("Research Complete: ${document.title}\n\n")
            append("Summary:\n${document.summary}\n\n")
            append("Sections:\n")
            document.sections.forEach { section ->
                append("• ${section.title}\n")
            }
            append("\nSources: ${document.sources.size} sources found")
        }
        binding.researchResultText.text = resultText
    }
    
    private fun showExportButton(document: com.example.app.data.model.ResearchDocument) {
        binding.exportButton.visibility = View.VISIBLE
        binding.exportButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val pdfFile = pdfExportManager.exportResearchToPdf(document)
                    Toast.makeText(
                        requireContext(), 
                        "PDF exported to: ${pdfFile.absolutePath}", 
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Optionally share the file
                    // sharePDFFile(pdfFile)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(), 
                        "PDF export failed: ${e.message}", 
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.researchButton.isEnabled = !loading
        binding.exportButton.visibility = if (loading) View.GONE else View.INVISIBLE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
