package com.researchsystem.app.research

import com.researchsystem.app.data.model.DeepSeekRequest
import com.researchsystem.app.data.model.Message
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class DeepSeekClient(private val apiKey: String) {
    private val retrofit: Retrofit
    private val service: DeepSeekService
    
    companion object {
        private const val BASE_URL = "https://api.deepseek.com/"
        private const val RESEARCH_SYSTEM_PROMPT = """
        You are an expert Android development research assistant. Your task is to provide comprehensive, 
        structured analysis for Android development projects. Focus on practical implementation details, 
        technical specifications, and real-world considerations for mobile development.
        
        Structure your responses with clear headings and provide specific, actionable information.
        Include relevant technologies, frameworks, libraries, and best practices for Android development.
        Consider performance, security, user experience, and platform-specific requirements.
        """
    }
    
    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
            
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        service = retrofit.create(DeepSeekService::class.java)
    }
    
    suspend fun generateResearchContent(
        context: String, 
        researchAreas: List<String>
    ): String {
        val prompt = buildResearchPrompt(context, researchAreas)
        
        val request = DeepSeekRequest(
            model = "deepseek-chat",
            messages = listOf(
                Message(role = "system", content = RESEARCH_SYSTEM_PROMPT),
                Message(role = "user", content = prompt)
            ),
            max_tokens = 4000
        )
        
        return try {
            val response = service.chatCompletion("Bearer $apiKey", request)
            response.choices.first().message.content
        } catch (e: Exception) {
            throw ResearchException("DeepSeek API error: ${e.message}", e)
        }
    }
    
    private fun buildResearchPrompt(context: String, areas: List<String>): String {
        return """
        Research Context: $context
        
        Please provide comprehensive analysis covering:
        ${areas.joinToString("\n") { "• $it" }}
        
        Structure the response with clear sections and include specific technical details,
        implementation steps, and relevant technologies for Android development.
        Focus on practical, actionable information that can be directly used in development planning.
        """
    }
}

class ResearchException(message: String, cause: Throwable? = null) : Exception(message, cause)
