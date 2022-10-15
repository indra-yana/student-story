package com.aad.storyapp.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aad.storyapp.datasource.local.dao.RemoteKeysDao
import com.aad.storyapp.datasource.local.dao.StoryDao
import com.aad.storyapp.datasource.local.entities.RemoteKeys
import com.aad.storyapp.datasource.local.entities.Story

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, AppDatabase::class.java, "quote_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}