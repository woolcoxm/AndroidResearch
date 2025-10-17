package com.example.app.research

import com.example.app.research.api.DeepSeekClient
import com.example.app.research.api.SerperClient
import com.example.app.research.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResearchManager(
    private val deepSeekClient: DeepSeekClient,
    private val serperClient: SerperClient
) {
    
    suspend fun conductResearch(request: ResearchRequest): ResearchDocument {
        return withContext(Dispatchers.IO) {
            try {
                // Phase 1: Initial web research
                val searchResults = conductWebResearch(request.prompt)
                
                // Phase 2: Generate comprehensive analysis
                val researchContent = generateComprehensiveAnalysis(
                    request.prompt, 
                    searchResults,
                    request
                )
                
                // Phase 3: Structure the document
                structureResearchDocument(
                    request.prompt,
                    researchContent,
                    searchResults,
                    request.researchDepth
                )
            } catch (e: Exception) {
                throw ResearchException("Research failed: ${e.message}")
            }
        }
    }
    
    private suspend fun conductWebResearch(prompt: String): List<SearchResult> {
        val searchQueries = generateSearchQueries(prompt)
        val allResults = mutableListOf<SearchResult>()
        
        searchQueries.forEach { query ->
            val results = serperClient.searchResearchTopics(query)
            allResults.addAll(results)
        }
        
        return allResults.distinctBy { it.link }
    }
    
    private fun generateSearchQueries(prompt: String): List<String> {
        return listOf(
            "Android development $prompt feasibility",
            "$prompt technical requirements mobile",
            "Android implementation $prompt",
            "$prompt similar projects case studies",
            "mobile development challenges $prompt"
        )
    }
    
    private suspend fun generateComprehensiveAnalysis(
        prompt: String,
        searchResults: List<SearchResult>,
        request: ResearchRequest
    ): String {
        val researchContext = buildResearchContext(prompt, searchResults)
        val researchAreas = determineResearchAreas(request)
        
        return deepSeekClient.generateResearchContent(
            researchContext, 
            researchAreas
        )
    }
    
    private fun buildResearchContext(prompt: String, searchResults: List<SearchResult>): String {
        val relevantSources = searchResults.take(5).joinToString("\n\n") { result ->
            "Source: ${result.title}\nURL: ${result.link}\nSummary: ${result.snippet}"
        }
        
        return """
        Research Topic: $prompt
        
        Relevant Sources Found:
        $relevantSources
        
        Please analyze this information and provide a comprehensive research document.
        """.trimIndent()
    }
    
    private fun determineResearchAreas(request: ResearchRequest): List<String> {
        val areas = mutableListOf<String>()
        
        if (request.includeFeasibility) {
            areas.addAll(listOf(
                "Market feasibility and competition analysis",
                "Technical feasibility assessment",
                "Resource requirements analysis"
            ))
        }
        
        if (request.includeRequirements) {
            areas.addAll(listOf(
                "Technical specifications and architecture",
                "Required technologies and frameworks",
                "Development team composition and skills needed"
            ))
        }
        
        if (request.includeImplementation) {
            areas.addAll(listOf(
                "Step-by-step implementation plan",
                "Development timeline with milestones",
                "Risk assessment and mitigation strategies"
            ))
        }
        
        return areas
    }
    
    private fun structureResearchDocument(
        prompt: String,
        researchContent: String,
        searchResults: List<SearchResult>,
        researchDepth: ResearchDepth
    ): ResearchDocument {
        val sections = parseResearchContent(researchContent)
        val sources = searchResults.take(10).map { result ->
            Source(
