package com.aad.storyapp.datasource.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.helper.Dummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/****************************************************
 * Created by Indra Muliana
 * On Thursday, 20/10/2022 22.45
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class StoryDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: StoryDao
    private val sampleStory = Dummy.generateDummyStoriesEntity()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        dao = database.storyDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveStory_Success() = runTest {
        dao.insertStory(sampleStory)

        val actualNews = dao.getAllStoryAsList(15, 1)
        assertTrue(actualNews.isNotEmpty())
    }
}