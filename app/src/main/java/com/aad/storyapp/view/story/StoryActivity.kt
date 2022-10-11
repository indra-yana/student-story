package com.aad.storyapp.view.story

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityStoryBinding
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.helper.visible
import com.aad.storyapp.listener.IOnItemClickListener
import com.aad.storyapp.model.Story
import com.aad.storyapp.model.User
import com.aad.storyapp.view.adapter.StoryAdapter
import com.aad.storyapp.view.auth.LoginActivity
import com.aad.storyapp.view.viewmodel.AuthViewModel
import com.aad.storyapp.view.viewmodel.StoryViewModel
import com.aad.storyapp.view.viewmodel.ViewModelFactory
import com.aad.storyapp.widget.ImageBannerWidget
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking


class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var storyAdapter: StoryAdapter
    private var user: User? = null

    companion object {
        private val TAG = StoryActivity::class.java.simpleName
        const val CREATE_STORY_RESULT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        setupStoriesRV()

        setupAction()
        setupVieModel()
    }

    private fun setupAction() {
        binding.apply {
            fabCreateStory.setOnClickListener {
                val intent = Intent(this@StoryActivity, CreateStoryActivity::class.java)
                intent.putExtra("user", user)
                launcherIntentCreateStory.launch(intent)
            }

            btnMenu.setOnClickListener {
                showPopupMenu(it)
            }
        }
    }

    private fun setupVieModel() {
        storyViewModel = ViewModelProvider(this, ViewModelFactory())[StoryViewModel::class.java]
        authViewModel = ViewModelProvider(this, ViewModelFactory())[AuthViewModel::class.java]

        storyViewModel.storiesResponse.observe(this) {

            with(binding) {
                pbLoading.visible(it is ResponseStatus.Loading)
                rvStories.visible(it !is ResponseStatus.Loading)
            }

            when (it) {
                is ResponseStatus.Loading -> {
                    // TODO: Handle loading state
                }
                is ResponseStatus.Success -> {
                    val stories = it.value.listStory
                    if (stories.size < 0) {
                        Toast.makeText(this@StoryActivity, getString(R.string.empty_data), Toast.LENGTH_SHORT).show()
                        return@observe
                    }

                    storyAdapter.addList(stories)

                    // Save stories result to display on app widget
                    val jsString = Gson().toJson(stories)
                    runBlocking {
                        storyViewModel.saveStories(jsString)
                    }
                    updateAppWidget()
                }
                is ResponseStatus.Failure -> {
                    Toast.makeText(this@StoryActivity, getString(R.string.fetch_data_failed, it.value?.message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(TAG, "setupVieModel: Unknown ResponseStatus")
                }
            }
        }

        storyViewModel.stories()

        authViewModel.session.observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                this.user = user
            }
        }
    }

    private fun initAdapter() {
        storyAdapter = StoryAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any, position: Int, view: View?) {
                    data as Story

                    val photo = itemBinding.ivStoryProfile
                    val name = itemBinding.tvStoryTitle
                    val description = itemBinding.tvStoryDescription
                    val date = itemBinding.tvStoryDate

                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@StoryActivity,
                        Pair(photo, "anim_story_profile"),
                        Pair(name, "anim_story_title"),
                        Pair(description, "anim_story_description"),
                        Pair(date, "anim_story_date"),
                    )

                    val intent = Intent(this@StoryActivity, DetailStoryActivity::class.java)
                    intent.putExtra("story", data)
                    startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    private fun setupStoriesRV() {
        with(binding) {
            rvStories.adapter = storyAdapter
            rvStories.layoutManager = LinearLayoutManager(this@StoryActivity)
            rvStories.setHasFixedSize(true)
        }
    }

    private fun showPopupMenu(view: View) {
        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_act_logout -> {
                        authViewModel.destroySession()
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_act_lang -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        return@OnMenuItemClickListener true
                    }
                }
                return@OnMenuItemClickListener false
            })
            show()
        }
    }

    private val launcherIntentCreateStory = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CREATE_STORY_RESULT) {
            val story = it.data?.getParcelableExtra<Story>("story")

            if (story != null) {
                storyAdapter.addData(story)
                binding.rvStories.smoothScrollToPosition(0)
            }
        }
    }

    private fun updateAppWidget() {
        val widgetManager = AppWidgetManager.getInstance(application)
        val ids = widgetManager.getAppWidgetIds(ComponentName(application, ImageBannerWidget::class.java))
        ImageBannerWidget().apply {
            onUpdate(this@StoryActivity, AppWidgetManager.getInstance(this@StoryActivity), ids)
        }
    }
}