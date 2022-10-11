package com.aad.storyapp.view.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aad.storyapp.listener.IOnItemClickListener

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 16.30
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindItem(data: Any, listener: IOnItemClickListener?)
}