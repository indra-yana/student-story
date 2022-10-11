package com.aad.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aad.storyapp.databinding.ItemStoryBinding
import com.aad.storyapp.helper.addAllFiltered
import com.aad.storyapp.listener.IOnItemClickListener
import com.aad.storyapp.model.Story
import com.aad.storyapp.view.adapter.viewholder.BaseViewHolder
import com.aad.storyapp.view.adapter.viewholder.StoryViewHolder

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 16.29
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: ArrayList<Story> = arrayListOf()
    var iOnItemClickListener: IOnItemClickListener? = null
    lateinit var itemBinding: ItemStoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        itemBinding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).apply {
            bindItem(itemList[position], iOnItemClickListener)
        }
    }

    override fun getItemCount(): Int = itemList.count()

    fun addList(itemList: ArrayList<Story>) {
        val oldCount: Int = itemCount
        this.itemList.addAllFiltered(itemList)
        notifyItemRangeInserted(oldCount, itemCount)
    }

    fun addData(story: Story) {
        this.itemList.add(0, story)
        notifyItemInserted(0)
    }
}