package com.aad.storyapp.di

import com.aad.storyapp.repository.AuthRepository
import com.aad.storyapp.repository.StoryRepository
import org.koin.dsl.module

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 20.31
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

fun provideStoryRepository(): StoryRepository {
    return StoryRepository()
}

fun provideAuthRepository(): AuthRepository {
    return AuthRepository()
}

val repositoryModule = module {
    single { provideStoryRepository() }
    single { provideAuthRepository() }
}