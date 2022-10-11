package com.aad.storyapp.listener

import android.view.View

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 16.18
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

interface IOnItemClickListener {
    fun onItemClicked(data: Any, position: Int, view: View?)
    fun onButtonRemoveItemClicked(data: Any, position: Int) {}
}