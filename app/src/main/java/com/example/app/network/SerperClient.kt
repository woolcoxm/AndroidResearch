package com.example.app.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Client for interacting with Serper.dev search API
 */
class SerperClient(private val apiKey: String) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * Searches for research topics using Serper API
     * @param query The search query
     * @return List of search results
     */
    suspend fun searchResearchTopics(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val requestBody = JSONObject().apply {
            put("q", query)
            put("num", 10)
            put("gl", "us")
            put("hl", "en")
        }.toString()
        
        val request = Request.Builder()
            .url("https://google.serper.dev/search")
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
        
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return@withContext emptyList()
                val jsonResponse = JSONObject(responseBody)
                val organicResults = jsonResponse.optJSONArray("organic") ?: return@withContext emptyList()
                
                val results = mutableListOf<SearchResult>()
                for (i in 0 until organicResults.length()) {
                    val result = organicResults.getJSONObject(i)
                    results.add(
                        SearchResult(
                            title = result.optString("title", ""),
                            link = result.optString("link", ""),
                            snippet = result.optString("snippet", "")
                        )
                    )
                }
                results
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
