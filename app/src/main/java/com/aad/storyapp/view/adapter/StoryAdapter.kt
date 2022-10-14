package com.aad.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.aad.storyapp.databinding.ItemStoryBinding
import com.aad.storyapp.listener.IOnItemClickListener
import com.aad.storyapp.model.Story
import com.aad.storyapp.view.adapter.viewholder.StoryViewHolder

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 16.29
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryAdapter : PagingDataAdapter<Story, StoryViewHolder>(DIFF_CALLBACK) {

    var iOnItemClickListener: IOnItemClickListener? = null
    lateinit var itemBinding: ItemStoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        itemBinding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position) as Story

        holder.bindItem(data, iOnItemClickListener)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}