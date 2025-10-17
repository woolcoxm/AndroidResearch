package com.researchsystem.app.research

import com.researchsystem.app.data.model.SearchRequest
import com.researchsystem.app.data.model.SearchResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class SerperClient(private val apiKey: String) {
    private val retrofit: Retrofit
    private val service: SerperService
    
    companion object {
        private const val BASE_URL = "https://google.serper.dev/"
    }
    
    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
            
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        service = retrofit.create(SerperService::class.java)
    }
    
    suspend fun searchResearchTopics(query: String): List<SearchResult> {
        val request = SearchRequest(
            q = query,
            num = 10
        )
        
        return try {
            val response = service.search(apiKey, request)
            response.organic.map { result ->
                SearchResult(
                    title = result.title,
                    link = result.link,
                    snippet = result.snippet
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
