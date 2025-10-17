package com.example.app.di

import android.content.Context
import com.example.app.data.local.ResearchDatabase
import com.example.app.data.repository.DeepSeekClient
import com.example.app.data.repository.SerperClient
import com.example.app.domain.PDFExportManager
import com.example.app.domain.ResearchManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideResearchDatabase(@ApplicationContext context: Context): ResearchDatabase {
        return ResearchDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideDeepSeekClient(): DeepSeekClient {
        // You should store API keys securely (e.g., in local.properties or use secrets gradle plugin)
        val apiKey = "your_deepseek_api_key_here" // Replace with actual secure storage
        return DeepSeekClient(apiKey)
    }
    
    @Provides
    @Singleton
    fun provideSerperClient(): SerperClient {
        // You should store API keys securely
        val apiKey = "your_serper_api_key_here" // Replace with actual secure storage
        return SerperClient(apiKey)
    }
    
    @Provides
    @Singleton
    fun provideResearchManager(
        deepSeekClient: DeepSeekClient,
        serperClient: SerperClient
    ): ResearchManager {
        return ResearchManager(deepSeekClient, serperClient)
    }
    
    @Provides
    @Singleton
    fun providePDFExportManager(@ApplicationContext context: Context): PDFExportManager {
        return PDFExportManager(context)
    }
}
