package com.aad.storyapp.view.story

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityStoryBinding
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.helper.visible
import com.aad.storyapp.listener.IOnItemClickListener
import com.aad.storyapp.model.Story
import com.aad.storyapp.model.User
import com.aad.storyapp.view.adapter.LoadingStateAdapter
import com.aad.storyapp.view.adapter.StoryAdapter
import com.aad.storyapp.view.auth.LoginActivity
import com.aad.storyapp.view.viewmodel.AuthViewModel
import com.aad.storyapp.view.viewmodel.StoryViewModel
import com.aad.storyapp.view.viewmodel.ViewModelFactory
import com.aad.storyapp.widget.ImageBannerWidget


class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var storyAdapter: StoryAdapter
    private var user: User? = null

    companion object {
        private val TAG = ListStoryActivity::class.java.simpleName
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
                val intent = Intent(this@ListStoryActivity, CreateStoryActivity::class.java)
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

        storyViewModel.storiesResponsePager.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }

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
                        this@ListStoryActivity,
                        Pair(photo, "anim_story_profile"),
                        Pair(name, "anim_story_title"),
                        Pair(description, "anim_story_description"),
                        Pair(date, "anim_story_date"),
                    )

                    val intent = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
                    intent.putExtra("story", data)
                    startActivity(intent, optionsCompat.toBundle())
                }
            }
        }

        storyAdapter.addLoadStateListener { loadState ->
            // show empty list
            if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) {
                binding.pbLoading.visible(true)
            } else {
                binding.pbLoading.visible(false)
                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this@ListStoryActivity, it.error.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupStoriesRV() {
        with(binding) {
            rvStories.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
            rvStories.layoutManager = LinearLayoutManager(this@ListStoryActivity)
            rvStories.setHasFixedSize(true)
        }
    }

    private fun showPopupMenu(view: View) {
        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_act_map -> {
                        startActivity(Intent(this@ListStoryActivity, MapsStoryActivity::class.java))
                        return@OnMenuItemClickListener true
                    }
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
                // TODO: Check this
                // storyAdapter.submitData(lifecycle, PagingData.from(listOf(story)))
                storyAdapter.snapshot().items.toMutableList().add(0, story)
                storyAdapter.notifyItemInserted(0)
                storyAdapter.refresh()

                binding.rvStories.smoothScrollToPosition(0)
            }
        }
    }

    private fun updateAppWidget() {
        val widgetManager = AppWidgetManager.getInstance(application)
        val ids = widgetManager.getAppWidgetIds(ComponentName(application, ImageBannerWidget::class.java))
        ImageBannerWidget().apply {
            onUpdate(this@ListStoryActivity, AppWidgetManager.getInstance(this@ListStoryActivity), ids)
        }
    }
}