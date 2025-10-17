package com.example.app.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Client for interacting with DeepSeek AI API
 */
class DeepSeekClient(private val apiKey: String) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    private val baseUrl = "https://api.deepseek.com/v1"
    
    /**
     * Generates research content using DeepSeek AI
     * @param context The research context and prompt
     * @param researchAreas List of areas to research
     * @return Generated research content as string
     */
    suspend fun generateResearchContent(
        context: String, 
        researchAreas: List<String>
    ): String = withContext(Dispatchers.IO) {
        val prompt = buildResearchPrompt(context, researchAreas)
        
        val requestBody = JSONObject().apply {
            put("model", "deepseek-chat")
            put("messages", listOf(
                mapOf(
                    "role" to "system",
                    "content" to RESEARCH_SYSTEM_PROMPT
                ),
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            ))
            put("max_tokens", 4000)
            put("temperature", 0.7)
        }.toString()
        
        val request = Request.Builder()
            .url("$baseUrl/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
        
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: throw ResearchException("Empty response body")
                val jsonResponse = JSONObject(responseBody)
                val content = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                content
            } else {
                throw ResearchException("DeepSeek API error: ${response.code} - ${response.message}")
            }
        } catch (e: Exception) {
            throw ResearchException("DeepSeek API call failed: ${e.message}")
        }
    }
    
    private fun buildResearchPrompt(context: String, areas: List<String>): String {
        return """
        Research Context: $context
        
        Please provide comprehensive analysis covering:
        ${areas.joinToString("\n") { "• $it" }}
        
        Structure the response with clear sections and include specific technical details,
        implementation steps, and relevant technologies for Android development.
        
        Format the response with clear section headers using markdown-style headers (##).
        """.trimIndent()
    }
    
    companion object {
        private const val RESEARCH_SYSTEM_PROMPT = """
        You are an expert Android development research assistant. Your task is to provide 
        comprehensive, structured research documents for Android project planning.
        
        Always structure your response with clear sections and provide actionable insights.
        Focus on Android-specific considerations, including:
        - Android architecture patterns (MVVM, MVI, Clean Architecture)
        - Recommended libraries and frameworks
        - Google Play Store requirements
        - Performance optimization
        - Security best practices
        - Development timeline estimates
        
        Be specific and provide concrete examples when possible.
        """
    }
}

/**
 * Custom exception for research-related errors
 */
class ResearchException(message: String) : Exception(message)
