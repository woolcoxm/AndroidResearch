package com.example.app.data.research

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

/**
 * Room database for storing research documents
 */
@Database(
    entities = [ResearchDocument::class],
    version = 1,
    exportSchema = false
)
abstract class ResearchDatabase : RoomDatabase() {
    abstract fun researchDao(): ResearchDao
    
    companion object {
        @Volatile
        private var INSTANCE: ResearchDatabase? = null
        
        fun getInstance(context: Context): ResearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResearchDatabase::class.java,
                    "research_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
