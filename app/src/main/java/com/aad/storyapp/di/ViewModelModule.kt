package com.aad.storyapp.di

import com.aad.storyapp.view.viewmodel.AuthViewModel
import com.aad.storyapp.view.viewmodel.StoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

fun provideStoryViewModel(): StoryViewModel {
    return StoryViewModel()
}

fun provideAuthViewModel(): AuthViewModel {
    return AuthViewModel()
}

val viewModelModule = module {
    viewModel { provideStoryViewModel() }
    viewModel { provideAuthViewModel() }
}