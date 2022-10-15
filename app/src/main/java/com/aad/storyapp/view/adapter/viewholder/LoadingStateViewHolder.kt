package com.aad.storyapp.view.adapter.viewholder

import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.aad.storyapp.databinding.ItemLoadingBinding
import com.aad.storyapp.listener.IOnItemClickListener

/****************************************************
 * Created by Indra Muliana
 * On Friday, 14/10/2022 22.40
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) : BaseViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        val loadState = data as LoadState

        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }

        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }
}