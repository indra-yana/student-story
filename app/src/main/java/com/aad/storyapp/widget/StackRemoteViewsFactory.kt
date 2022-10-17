package com.aad.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.aad.storyapp.BaseApplication
import com.aad.storyapp.R
import com.aad.storyapp.datasource.local.AppDatabase
import com.aad.storyapp.datasource.local.AppPreferences
import com.aad.storyapp.helper.DataClassMapper
import com.aad.storyapp.helper.rotateBitmap
import com.aad.storyapp.model.Story
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject


/****************************************************
 * Created by Indra Muliana
 * On Saturday, 10/09/2022 21.55
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val TAG = this::class.java.simpleName
    private lateinit var mStories: List<Story>
    private val preferences: AppPreferences by inject(AppPreferences::class.java)
    private val database: AppDatabase by inject(AppDatabase::class.java)

    override fun onCreate() {
        Log.d(TAG, "onCreate: Widget created!")
    }

    override fun onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged: Widget data set changed!")
        runBlocking {
//            mStories = preferences.getStories().first() ?: arrayListOf()
            mStories = DataClassMapper.mapStoryEntityToStoryModel(database.storyDao().getAllStoryAsList(15, 1 * 15))
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mStories.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        if (mStories.isNotEmpty()) {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(mContext)
                .asBitmap()
                .load(mStories[position].photoUrl)
                .submit(450, 450)

            val bitmap = rotateBitmap(futureTarget.get(), true)
            rv.setImageViewBitmap(R.id.imageView, bitmap)
        }

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}