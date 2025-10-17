package com.researchsystem.app.research

import com.researchsystem.app.data.model.DeepSeekRequest
import com.researchsystem.app.data.model.DeepSeekResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: DeepSeekRequest
    ): DeepSeekResponse
}
