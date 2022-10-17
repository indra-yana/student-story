package com.aad.storyapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityRegisterBinding
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.enable
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.helper.visible
import com.aad.storyapp.view.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModel()

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupViewModel()
    }

    private fun setupAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString().trim()
                val email = edRegisterEmail.text.toString().trim()
                val password = edRegisterPassword.text.toString().trim()

                when {
                    name.isEmpty() -> {
                        edLayoutName.error = getString(R.string.validation_name_required)
                    }
                    !edRegisterEmail.validate(email) || !edRegisterPassword.validate(password) -> {
                        Toast.makeText(this@RegisterActivity, getString(R.string.validation_failed), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        edLayoutName.error = ""
                        edLayoutEmail.error = ""
                        edLayoutPassword.error = ""

                        authViewModel.register(name, email, password)
                    }
                }
            }

            btnToLogin.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupViewModel() {
//        authViewModel = ViewModelProvider(this, ViewModelFactory())[AuthViewModel::class.java]
        authViewModel.registerResponse.observe(this) {

            with(binding) {
                pbLoading.visible(it is ResponseStatus.Loading)
                btnRegister.enable(it !is ResponseStatus.Loading)
            }

            when (it) {
                is ResponseStatus.Loading -> {
                    // TODO: Do something when loading
                }
                is ResponseStatus.Success -> {
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_successfully, it.value.message), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                is ResponseStatus.Failure -> {
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_failed, it.value?.message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(TAG, "setupViewModel: Unknown ResponseStatus")
                }
            }
        }
    }
}