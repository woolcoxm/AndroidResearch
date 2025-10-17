package com.example.app.research.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

interface SerperService {
    @Headers("Content-Type: application/json")
    @POST("search")
    suspend fun search(@Body request: SearchRequest): SearchResponse
}

data class SearchRequest(
    @SerializedName("q") val q: String,
    @SerializedName("num") val num: Int = 10,
    @SerializedName("gl") val gl: String = "us",
    @SerializedName("hl") val hl: String = "en"
)

data class SearchResponse(
    @SerializedName("organic") val organic: List<OrganicResult>?
)

data class OrganicResult(
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("snippet") val snippet: String
)

class SerperClient(private val apiKey: String) {
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
            .baseUrl("https://google.serper.dev/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val service: SerperService by lazy {
        retrofit.create(SerperService::class.java)
    }
    
    suspend fun searchResearchTopics(query: String): List<SearchResult> {
        val request = SearchRequest(q = query, num = 10)
        
        return try {
            val response = service.search(request)
            response.organic?.map { result ->
                SearchResult(
                    title = result.title,
                    link = result.link,
                    snippet = result.snippet
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
