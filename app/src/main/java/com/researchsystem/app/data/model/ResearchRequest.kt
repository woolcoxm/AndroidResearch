package com.researchsystem.app.data.model

data class ResearchRequest(
    val prompt: String,
    val researchDepth: ResearchDepth = ResearchDepth.COMPREHENSIVE,
    val targetPlatforms: List<String> = listOf("Android"),
    val includeFeasibility: Boolean = true,
    val includeRequirements: Boolean = true,
    val includeImplementation: Boolean = true
)

enum class ResearchDepth {
    QUICK, STANDARD, COMPREHENSIVE
}
