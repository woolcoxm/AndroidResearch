package com.example.app.research.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

interface DeepSeekService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: DeepSeekRequest
    ): DeepSeekResponse
}

data class DeepSeekRequest(
    @SerializedName("model") val model: String = "deepseek-chat",
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 4000,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class DeepSeekResponse(
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("usage") val usage: Usage?
)

data class Choice(
    @SerializedName("message") val message: Message,
    @SerializedName("finish_reason") val finishReason: String
)

data class Usage(
    @SerializedName("total_tokens") val totalTokens: Int
)

class DeepSeekClient(private val apiKey: String) {
    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
            
        Retrofit.Builder()
            .baseUrl("https://api.deepseek.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val service: DeepSeekService by lazy {
        retrofit.create(DeepSeekService::class.java)
    }
    
    suspend fun generateResearchContent(
        context: String, 
        researchAreas: List<String>
    ): String {
        val prompt = buildResearchPrompt(context, researchAreas)
        
        val request = DeepSeekRequest(
            messages = listOf(
                Message(role = "system", content = RESEARCH_SYSTEM_PROMPT),
                Message(role = "user", content = prompt)
            )
        )
        
        return try {
            val response = service.chatCompletion("Bearer $apiKey", request)
            if (response.choices.isNotEmpty()) {
                response.choices.first().message.content
            } else {
                throw ResearchException("No response from DeepSeek API")
            }
        } catch (e: Exception) {
            throw ResearchException("DeepSeek API error: ${e.message}")
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
        comprehensive, well-structured research documents that cover technical feasibility, 
        requirements, implementation plans, and risk analysis for Android projects.
        
        Always structure your response with clear sections and provide actionable insights.
        Focus on practical Android development considerations, including:
        - Appropriate architecture patterns (MVVM, MVI, etc.)
        - Recommended libraries and frameworks
        - Performance considerations
        - Google Play Store requirements
        - Development timeline estimates
        - Potential challenges and solutions
        
        Be specific and provide concrete examples when possible.
        """
    }
}
