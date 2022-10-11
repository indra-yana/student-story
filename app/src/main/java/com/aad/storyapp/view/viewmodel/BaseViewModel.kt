package com.aad.storyapp.view.viewmodel

import androidx.lifecycle.ViewModel
import com.aad.storyapp.BaseApplication
import com.aad.storyapp.repository.AuthRepository
import com.aad.storyapp.repository.StoryRepository

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 09.50
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

abstract class BaseViewModel : ViewModel() {

    protected val authRepository: AuthRepository by lazy { BaseApplication.authRepository }
    protected val storyRepository: StoryRepository by lazy { BaseApplication.storyRepository }

}