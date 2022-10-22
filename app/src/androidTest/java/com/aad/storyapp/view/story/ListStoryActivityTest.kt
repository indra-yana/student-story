package com.aad.storyapp.view.story

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.aad.storyapp.R
import com.aad.storyapp.helper.Constant
import com.aad.storyapp.helper.EspressoIdlingResource
import com.aad.storyapp.helper.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/****************************************************
 * Created by Indra Muliana
 * On Friday, 21/10/2022 22.28
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class ListStoryActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(ListStoryActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        Constant.BASE_URL = "http://127.0.0.1:8080/v1/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getListStory_Success() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))

        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withText("Testing Matched View")).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5))
        onView(withText("Testing Scroll Item")).check(matches(isDisplayed()))
    }

}


