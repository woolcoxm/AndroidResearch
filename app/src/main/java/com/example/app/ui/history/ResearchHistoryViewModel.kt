package com.example.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.local.ResearchDao
import com.example.app.data.model.ResearchDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResearchHistoryViewModel @Inject constructor(
    private val researchDao: ResearchDao
) : ViewModel() {
    
    val researchHistory: Flow<List<ResearchDocument>> = researchDao.getAllDocuments()
    
    fun deleteDocument(document: ResearchDocument) {
        viewModelScope.launch {
            researchDao.deleteDocument(document)
        }
    }
}
