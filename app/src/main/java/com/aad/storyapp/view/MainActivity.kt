package com.aad.storyapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.aad.storyapp.R
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.view.auth.LoginActivity
import com.aad.storyapp.view.story.StoryActivity
import com.aad.storyapp.view.viewmodel.AuthViewModel
import com.aad.storyapp.view.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setupView(supportActionBar)

        setupViewModel()
    }

    private fun setupViewModel() {
        authViewModel = ViewModelProvider(this, ViewModelFactory())[AuthViewModel::class.java]
        authViewModel.session.observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, StoryActivity::class.java))
            }

            finish()
        }
    }
}