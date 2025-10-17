package com.example.app.data.research

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for research documents
 */
@Dao
interface ResearchDao {
    
    @Query("SELECT * FROM research_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<ResearchDocument>>
    
    @Query("SELECT * FROM research_documents WHERE id = :documentId")
    suspend fun getDocumentById(documentId: String): ResearchDocument?
    
    @Insert
    suspend fun insertDocument(document: ResearchDocument)
    
    @Update
    suspend fun updateDocument(document: ResearchDocument)
    
    @Query("DELETE FROM research_documents WHERE id = :documentId")
    suspend fun deleteDocument(documentId: String)
    
    @Query("DELETE FROM research_documents")
    suspend fun deleteAllDocuments()
}
