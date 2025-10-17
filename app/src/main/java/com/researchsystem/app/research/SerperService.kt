package com.researchsystem.app.research

import com.researchsystem.app.data.model.SearchRequest
import com.researchsystem.app.data.model.SearchResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SerperService {
    @POST("search")
    suspend fun search(
        @Header("X-API-KEY") apiKey: String,
        @Body request: SearchRequest
    ): SearchResponse
}
