package com.example.app.research.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

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

@Entity(tableName = "research_documents")
@TypeConverters(ResearchConverters::class)
data class ResearchDocument(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val sections: List<ResearchSection>,
    val sources: List<Source>,
    val timestamp: Long = System.currentTimeMillis(),
    val summary: String,
    val researchDepth: ResearchDepth = ResearchDepth.STANDARD
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

data class SearchResult(
    val title: String,
    val link: String,
    val snippet: String
)

class ResearchException(message: String) : Exception(message)

class ResearchConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromSectionsList(sections: List<ResearchSection>): String {
        return gson.toJson(sections)
    }
    
    @TypeConverter
    fun toSectionsList(sectionsString: String): List<ResearchSection> {
        val listType = object : TypeToken<List<ResearchSection>>() {}.type
        return gson.fromJson(sectionsString, listType)
    }
    
    @TypeConverter
    fun fromSourcesList(sources: List<Source>): String {
        return gson.toJson(sources)
    }
    
    @TypeConverter
    fun toSourcesList(sourcesString: String): List<Source> {
        val listType = object : TypeToken<List<Source>>() {}.type
        return gson.fromJson(sourcesString, listType)
    }
    
    @TypeConverter
    fun fromSectionType(sectionType: SectionType): String {
        return sectionType.name
    }
    
    @TypeConverter
    fun toSectionType(sectionTypeString: String): SectionType {
        return SectionType.valueOf(sectionTypeString)
    }
    
    @TypeConverter
    fun fromResearchDepth(researchDepth: ResearchDepth): String {
        return researchDepth.name
    }
    
    @TypeConverter
    fun toResearchDepth(researchDepthString: String): ResearchDepth {
        return ResearchDepth.valueOf(researchDepthString)
    }
    
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }
    
    @TypeConverter
    fun toStringList(listString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(listString, listType)
    }
}
