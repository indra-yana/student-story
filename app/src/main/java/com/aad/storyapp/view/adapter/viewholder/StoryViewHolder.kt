package com.aad.storyapp.view.adapter.viewholder

import com.aad.storyapp.R
import com.aad.storyapp.databinding.ItemStoryBinding
import com.aad.storyapp.helper.loadImage
import com.aad.storyapp.helper.withDateFormat
import com.aad.storyapp.listener.IOnItemClickListener
import com.aad.storyapp.model.Story

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 16.33
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryViewHolder(private val binding: ItemStoryBinding) : BaseViewHolder(binding.root) {
    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as Story

        binding.apply {
            tvStoryTitle.text = data.name
            tvStoryDescription.text = data.description
            tvStoryDate.text = root.context.getString(R.string.createdAtFormat, data.createdAt.withDateFormat())
            ivStoryProfile.loadImage(data.photoUrl)

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition, it)
            }
        }
    }

}