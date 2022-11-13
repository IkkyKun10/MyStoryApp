package com.riezki.storyapp.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.riezki.storyapp.R
import com.riezki.storyapp.model.preference.DataStorePreference
import com.riezki.storyapp.ui.authenticasion.login.dataStore
import com.riezki.storyapp.ui.widget.StoryWidgetData.getStory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Implementation of App Widget functionality.
 */
class ImageBannerWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.riezki.storyapp.TOAST_ACTION"
        private const val UPDATE_ACTION = "com.riezki.storyapp.UPDATE_ACTION"
        const val EXTRA_ITEM = "com.riezki.storyapp.EXTRA_ITEM"

        private fun getPendingSelfIntent(context: Context, appWidgetId: Int, action: String): PendingIntent {
            val intent = Intent(context, ImageBannerWidget::class.java)
            intent.action = action
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            return PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else {
                    0
                }
            )
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val intent = Intent(context, StackWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        val views = RemoteViews(context.packageName, R.layout.image_banner_widget)
        views.setRemoteAdapter(R.id.stack_view, intent)
        views.setEmptyView(R.id.stack_view, R.id.empty_view)

        views.setPendingIntentTemplate(R.id.stack_view, getPendingSelfIntent(context, appWidgetId, TOAST_ACTION))
        views.setOnClickPendingIntent(R.id.btn_update_story, getPendingSelfIntent(context, appWidgetId, UPDATE_ACTION))
        views.setOnClickPendingIntent(R.id.btn_update_story_new, getPendingSelfIntent(context, appWidgetId, UPDATE_ACTION))

        appWidgetManager.updateAppWidget(appWidgetId, views)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val dataStorePref = context?.let {DataStorePreference(it.dataStore) }
        //val token = dataStorePref?.readTokenFromDataStore?.asLiveData()?.toString()
        val token = runBlocking { dataStorePref?.readTokenFromDataStore?.first() }

        if (intent?.action != null) {
            when (intent.action) {
                TOAST_ACTION -> {
                    val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
                    Toast.makeText(context, context?.getString(R.string.storyItemNumber, viewIndex), Toast.LENGTH_SHORT).show()
                }

                UPDATE_ACTION -> {
                    val appWidgetId = intent.getSerializableExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) as Int
                    getStory("Bearer $token", context, appWidgetId)
                }
            }
        }

    }
}