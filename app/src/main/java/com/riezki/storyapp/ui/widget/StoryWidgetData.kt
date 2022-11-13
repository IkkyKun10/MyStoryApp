package com.riezki.storyapp.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.riezki.storyapp.R
import com.riezki.storyapp.model.response.ListStoryItemResponse
import com.riezki.storyapp.model.response.ListStoryResponse
import com.riezki.storyapp.network.api.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal object StoryWidgetData {

    private var widgetImages = ArrayList<Bitmap>()

    private val getCount get() = widgetImages.count()

    val getImages get() = widgetImages

    private var maxImages = 6

    private fun removeAll() = widgetImages.clear()

    private fun addImage(img: Bitmap) {
        widgetImages.add(img)
    }

    fun getStory(token: String?, context: Context?, appWidgetId: Int, page: Int = 1, size: Int = maxImages) {
        removeAll()
        val client = token?.let { ApiConfig().getApiService().getListImageUser(it, page, size) }
        client?.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(call: Call<ListStoryResponse>, response: Response<ListStoryResponse>) {
                val imgResponse = response.body()
                if (response.isSuccessful) {
                    getImageFromServer(context, appWidgetId, imgResponse?.listStory)
                } else {
                    Log.e("StoryWidgetData", "onFailure x: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.e("StoryWidgetData", "onFailure y: ${t.message}")
            }

        })
    }

    private fun getImageFromServer(context: Context?, appWidgetId: Int, stories: List<ListStoryItemResponse?>?) {
        stories?.forEach {
            if (getCount <= maxImages) {
                Glide.with(context!!)
                    .asBitmap()
                    .load(it?.photoUrl)
                    .apply(RequestOptions().override(400, 400))
                    .centerCrop()
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            addImage(resource)
                            updateWidget(context, appWidgetId)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    })
            } else return
        }
    }

    private fun updateWidget(context: Context, appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
    }
}
