package com.researchsystem.app.data.model

data class ResearchDocument(
    val id: String,
    val title: String,
    val sections: List<ResearchSection>,
    val sources: List<Source>,
    val timestamp: Long,
    val summary: String
)

data class ResearchSection(
    val title: String,
    val content: String,
    val sectionType: SectionType
)

enum class SectionType {
    FEASIBILITY_ANALYSIS, REQUIREMENTS, IMPLEMENTATION_PLAN, 
    TECHNICAL_STACK, TIMELINE_ESTIMATE, RISK_ANALYSIS, SUMMARY
}

data class Source(
    val title: String,
    val url: String,
    val snippet: String
)
