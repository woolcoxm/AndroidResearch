package com.example.app.data.research

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Main research document entity that stores complete research results
 */
@Entity(tableName = "research_documents")
data class ResearchDocument(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val sections: List<ResearchSection>,
    val sources: List<Source>,
    val timestamp: Long = System.currentTimeMillis(),
    val summary: String = "",
    val researchDepth: ResearchDepth = ResearchDepth.STANDARD
)

/**
 * Represents a section within a research document
 */
data class ResearchSection(
    val title: String,
    val content: String,
    val sectionType: SectionType
)

/**
 * Represents a source/reference used in research
 */
data class Source(
    val title: String,
    val url: String,
    val snippet: String
)

/**
 * Types of sections in research documents
 */
enum class SectionType {
    FEASIBILITY_ANALYSIS, REQUIREMENTS, IMPLEMENTATION_PLAN, 
    TECHNICAL_STACK, TIMELINE_ESTIMATE, RISK_ANALYSIS, SUMMARY
}

/**
 * Depth levels for research
 */
enum class ResearchDepth {
    QUICK, STANDARD, COMPREHENSIVE
}

/**
 * Request parameters for conducting research
 */
data class ResearchRequest(
    val prompt: String,
    val researchDepth: ResearchDepth = ResearchDepth.STANDARD,
    val targetPlatforms: List<String> = listOf("Android"),
    val includeFeasibility: Boolean = true,
    val includeRequirements: Boolean = true,
    val includeImplementation: Boolean = true
)
