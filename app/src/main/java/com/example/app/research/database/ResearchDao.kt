package com.example.app.research.database

import androidx.room.*
import com.example.app.research.data.ResearchDocument
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchDao {
    @Query("SELECT * FROM research_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<ResearchDocument>>
    
    @Query("SELECT * FROM research_documents WHERE id = :id")
    suspend fun getDocumentById(id: String): ResearchDocument?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: ResearchDocument)
    
    @Delete
    suspend fun deleteDocument(document: ResearchDocument)
    
    @Query("DELETE FROM research_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)
}
