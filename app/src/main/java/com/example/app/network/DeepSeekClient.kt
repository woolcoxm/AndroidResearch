package com.example.app.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
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
    
    companion object {
        private const val BASE_URL = "https://api.deepseek.com/v1"
        private const val RESEARCH_SYSTEM_PROMPT = """You are an expert Android development research assistant. 
        Provide comprehensive, structured analysis for Android projects including:
        - Technical feasibility assessment
        - Required technologies and architecture
        - Implementation steps and best practices
        - Timeline estimates
        - Risk analysis
        
        Structure your response with clear sections and bullet points. Focus on Android-specific considerations."""
    }
    
    /**
     * Generates research content using DeepSeek API
     */
    suspend fun generateResearchContent(
        context: String, 
        researchAreas: List<String>
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildResearchPrompt(context, researchAreas)
            
            val requestBody = JSONObject().apply {
                put("model", "deepseek-chat")
                put("messages", listOf(
                    mapOf("role" to "system", "content" to RESEARCH_SYSTEM_PROMPT),
                    mapOf("role" to "user", "content" to prompt)
                ))
                put("max_tokens", 4000)
                put("temperature", 0.7)
            }.toString()
            
            val request = Request.Builder()
                .url("$BASE_URL
