package com.aad.storyapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aad.storyapp.datasource.remote.response.ApiResponse
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.datasource.remote.response.StoryResponse
import com.aad.storyapp.model.Story
import com.aad.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 10.02
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryViewModel(private var repo: StoryRepository? = null) : BaseViewModel() {

    private val _storyCreateResponse: MutableLiveData<ResponseStatus<ApiResponse>> = MutableLiveData()
    val storyCreateResponse: LiveData<ResponseStatus<ApiResponse>> get() = _storyCreateResponse

    private val _storiesResponse: MutableLiveData<ResponseStatus<StoryResponse>> = MutableLiveData()
    val storiesResponse: LiveData<ResponseStatus<StoryResponse>> get() = _storiesResponse

    val storiesResponsePager: LiveData<PagingData<Story>>
        get() = if (repo != null) repo?.storiesWithPagination()?.cachedIn(viewModelScope)!! else storyRepository.storiesWithPagination().cachedIn(viewModelScope)

    fun create(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody) = viewModelScope.launch {
        _storyCreateResponse.value = ResponseStatus.Loading
        _storyCreateResponse.value = storyRepository.create(photo, description, lat, lon)
    }

    fun stories(page: Int = 1, size: Int = 10, location: Int = 0) = viewModelScope.launch {
        _storiesResponse.value = ResponseStatus.Loading
        _storiesResponse.value = storyRepository.stories(page, size, location)
    }

    fun saveStories(stories: String) = viewModelScope.launch {
        storyRepository.saveStories(stories)
    }
}