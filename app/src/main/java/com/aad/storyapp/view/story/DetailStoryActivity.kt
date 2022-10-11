package com.aad.storyapp.view.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityDetailStoryBinding
import com.aad.storyapp.helper.loadImage
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.helper.withDateFormat
import com.aad.storyapp.model.Story

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<Story>("story")!!

        setupView(story)
    }

    private fun setupView(story: Story) {
        with(binding) {
            ivStoryProfile.loadImage(story.photoUrl)
            tvStoryTitle.text = story.name
            tvStoryDescription.text = story.description
            tvStoryDate.text = getString(R.string.createdAtFormat, story.createdAt.withDateFormat())

            btnBack.setOnClickListener {
                onBackPressed()
                finish()
            }
        }
    }
}