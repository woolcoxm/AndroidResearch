package com.researchsystem.app.research

import com.researchsystem.app.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ResearchManager(
    private val deepSeekClient: DeepSeekClient,
    private val serperClient: SerperClient
) {
    
    suspend fun conductResearch(request: ResearchRequest): ResearchDocument {
        return withContext(Dispatchers.IO) {
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
                searchResults
            )
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
            "mobile development challenges $prompt",
            "$prompt Kotlin Android libraries",
            "$prompt Google Play Store requirements"
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
                "Technical feasibility assessment for Android platform",
                "Resource requirements analysis",
                "Market demand and target audience analysis"
            ))
        }
        
        if (request.includeRequirements) {
            areas.addAll(listOf(
                "Technical specifications and architecture",
                "Required technologies and frameworks for Android",
                "Development team composition and skills needed",
                "Hardware and software requirements",
                "Google Play Store submission requirements"
            ))
        }
        
        if (request.includeImplementation) {
            areas.addAll(listOf(
                "Step-by-step implementation plan for Android",
                "Development timeline with milestones",
                "Risk assessment and mitigation strategies",
                "Testing strategy and quality assurance",
                "Deployment and maintenance plan"
            ))
        }
        
        return areas
    }
    
    private fun structureResearchDocument(
        prompt: String,
        researchContent: String,
        searchResults: List<SearchResult>
    ): ResearchDocument {
        val sections = parseResearchContent(researchContent)
        val sources = searchResults.map { result ->
            Source(
                title = result.title,
                url = result.link,
                snippet = result.snippet
            )
        }
        
        return ResearchDocument(
            id = UUID.randomUUID().toString(),
            title = "Research: $prompt",
            sections = sections,
            sources = sources,
            timestamp = System.currentTimeMillis(),
            summary = generateSummary(sections)
        )
    }
    
    private fun parseResearchContent(content: String): List<ResearchSection> {
        val sections = mutableListOf<ResearchSection>()
        val lines = content.lines()
        var currentTitle = ""
        var currentContent = StringBuilder()
        var currentType = SectionType.SUMMARY
        
        for (line in lines) {
            when {
                line.startsWith("# ") -> {
                    if (currentTitle.isNotEmpty()) {
                        sections.add(ResearchSection(currentTitle, currentContent.toString().trim(), currentType))
                    }
                    currentTitle = line.removePrefix("# ").trim()
                    currentContent = StringBuilder()
                    currentType = determineSectionType(currentTitle)
                }
                line.startsWith("## ") -> {
                    if (currentTitle.isNotEmpty()) {
                        sections.add(ResearchSection(currentTitle, currentContent.toString().trim(), currentType))
                    }
                    currentTitle = line.removePrefix("## ").trim()
                    currentContent = StringBuilder()
                    currentType = determineSectionType(currentTitle)
                }
                else -> {
                    currentContent.appendLine(line)
                }
            }
        }
        
        if (currentTitle.isNotEmpty()) {
            sections.add(ResearchSection(currentTitle, currentContent.toString().trim(), currentType))
        }
        
        return sections
    }
    
    private fun
