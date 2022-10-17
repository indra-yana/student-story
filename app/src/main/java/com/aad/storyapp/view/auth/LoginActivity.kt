package com.aad.storyapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityLoginBinding
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.enable
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.helper.visible
import com.aad.storyapp.view.story.ListStoryActivity
import com.aad.storyapp.view.viewmodel.AuthViewModel
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModel()

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupViewModel()
    }

    private fun setupAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = edLoginEmail.text.toString().trim()
                val password = edLoginPassword.text.toString().trim()

                when {
                    !edLoginEmail.validate(email) || !edLoginPassword.validate(password) -> {
                        Toast.makeText(this@LoginActivity, getString(R.string.validation_failed), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        edLayoutEmail.error = ""
                        edLayoutPassword.error = ""

                        authViewModel.login(email, password)
                    }
                }
            }

            btnToRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun setupViewModel() {
//        authViewModel = ViewModelProvider(this, ViewModelFactory())[AuthViewModel::class.java]
        authViewModel.loginResponse.observe(this) {

            with(binding) {
                pbLoading.visible(it is ResponseStatus.Loading)
                btnLogin.enable(it !is ResponseStatus.Loading)
            }

            when (it) {
                is ResponseStatus.Loading -> {
                    // TODO: Do something when loading
                }
                is ResponseStatus.Success -> {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_successfully, it.value.message), Toast.LENGTH_SHORT).show()

                    runBlocking {
                        authViewModel.saveSession(it.value.loginResult)
                    }

                    val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                is ResponseStatus.Failure -> {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_failed, it.value?.message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(TAG, "setupViewModel: Unknown ResponseStatus")
                }
            }
        }
    }

}