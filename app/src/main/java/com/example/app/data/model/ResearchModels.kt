package com.example.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data model for research requests
 */
data class ResearchRequest(
    val prompt: String,
    val researchDepth: ResearchDepth = ResearchDepth.COMPREHENSIVE,
    val targetPlatforms: List<String> = listOf("Android"),
    val includeFeasibility: Boolean = true,
    val includeRequirements: Boolean = true,
    val includeImplementation: Boolean = true
)

/**
 * Enum representing the depth of research to be conducted
 */
enum class ResearchDepth {
    QUICK, STANDARD, COMPREHENSIVE
}

/**
 * Entity representing a complete research document
 */
@Entity(tableName = "research_documents")
data class ResearchDocument(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val sections: List<ResearchSection>,
    val sources: List<Source>,
    val timestamp: Long = System.currentTimeMillis(),
    val summary: String,
    val researchDepth: ResearchDepth
)

/**
 * Data class representing a section within a research document
 */
data class ResearchSection(
    val title: String,
    val content: String,
    val sectionType: SectionType
)

/**
 * Enum representing different types of research sections
 */
enum class SectionType {
    FEASIBILITY_ANALYSIS, REQUIREMENTS, IMPLEMENTATION_PLAN, 
    TECHNICAL_STACK, TIMELINE_ESTIMATE, RISK_ANALYSIS, SUMMARY
}

/**
 * Data class representing a research source
 */
data class Source(
    val title: String,
    val url: String,
    val snippet: String
)

/**
 * Data class representing a web search result
 */
data class SearchResult(
    val title: String,
    val link: String,
    val snippet: String
)
