package com.example.app.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app.data.model.ResearchDocument
import com.example.app.databinding.ItemResearchHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class ResearchHistoryAdapter(
    private val onItemClick: (ResearchDocument) -> Unit
) : ListAdapter<ResearchDocument, ResearchHistoryAdapter.ResearchViewHolder>(DiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchViewHolder {
        val binding = ItemResearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResearchViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ResearchViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(document)
    }
    
    inner class ResearchViewHolder(
        private val binding: ItemResearchHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }
