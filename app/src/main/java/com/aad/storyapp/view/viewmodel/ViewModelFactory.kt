package com.aad.storyapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 09.59
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel() as T
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> StoryViewModel() as T
            else -> throw IllegalArgumentException("Undefined ViewModelClass!")
        }
    }
}