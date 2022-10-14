package com.aad.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.aad.storyapp.databinding.ItemLoadingBinding
import com.aad.storyapp.view.adapter.viewholder.LoadingStateViewHolder

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 08/10/2022 20.50
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }
}