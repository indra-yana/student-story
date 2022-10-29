package com.aad.storyapp.data

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.dao.RemoteKeysDao
import com.aad.storyapp.datasource.local.dao.StoryDao
import org.mockito.Mockito

/****************************************************
 * Created by Indra Muliana
 * On Friday, 28/10/2022 19.12
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class FakeAppDatabase : AppDatabase() {
    override fun storyDao(): StoryDao {
        return FakeStoryDaoImpl()
    }

    override fun remoteKeysDao(): RemoteKeysDao {
        return FakeRemoteKeysDaoImpl()
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        return Mockito.mock(SupportSQLiteOpenHelper::class.java)
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return Mockito.mock(InvalidationTracker::class.java)
    }

    override fun clearAllTables() {}
}