package com.example.mireproductorconwidgetinteractivo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


class MusicPlayerWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.mireproductorconwidgetinteractivo.ACTION_WIDGET_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.mireproductorconwidgetinteractivo.ACTION_WIDGET_NEXT"
        const val ACTION_PREVIOUS = "com.example.mireproductorconwidgetinteractivo.ACTION_WIDGET_PREVIOUS"
        const val ACTION_UPDATE_WIDGET = "com.example.mireproductorconwidgetinteractivo.ACTION_UPDATE_WIDGET"

        private var currentSongTitle = "Retro Player"
        private var currentArtist = "▶ Press Start to Play"
        private var isPlaying = false


        fun updateWidgetInfo(context: Context, title: String, artist: String, playing: Boolean) {
            currentSongTitle = title
            currentArtist = artist
            isPlaying = playing

            val intent = Intent(context, MusicPlayerWidget::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }
        

        fun isCurrentlyPlaying(): Boolean = isPlaying
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        if (MusicPlayerService.isServiceRunning()) {
            val song = MusicPlayerService.getCurrentSong()
            if (song != null) {
                currentSongTitle = song.title
                currentArtist = song.artist
            }
            isPlaying = MusicPlayerService.isPlaying()
        }
        
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {

                val serviceIntent = Intent(context, MusicPlayerService::class.java).apply {
                    action = MusicPlayerService.ACTION_PLAY_PAUSE
                }
                context.startService(serviceIntent)
                

            }
            ACTION_NEXT -> {

                val serviceIntent = Intent(context, MusicPlayerService::class.java).apply {
                    action = MusicPlayerService.ACTION_NEXT
                }
                context.startService(serviceIntent)
            }
            ACTION_PREVIOUS -> {

                val serviceIntent = Intent(context, MusicPlayerService::class.java).apply {
                    action = MusicPlayerService.ACTION_PREVIOUS
                }
                context.startService(serviceIntent)
            }
            ACTION_UPDATE_WIDGET -> {
                updateAllWidgets(context)
            }
        }
    }
    
    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicPlayerWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        onUpdate(context, appWidgetManager, appWidgetIds)
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_music_player)
        

        views.setTextViewText(R.id.widgetSongTitle, currentSongTitle)
        

        val playPauseIcon = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        views.setImageViewResource(R.id.widgetBtnPlayPause, playPauseIcon)
        

        val playPauseIntent = Intent(context, MusicPlayerWidget::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getBroadcast(
            context, 0, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetBtnPlayPause, playPausePendingIntent)
        

        val nextIntent = Intent(context, MusicPlayerWidget::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context, 1, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetBtnNext, nextPendingIntent)
        

        val previousIntent = Intent(context, MusicPlayerWidget::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getBroadcast(
            context, 2, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetBtnPrevious, previousPendingIntent)
        

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context, 3, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetSongTitle, mainPendingIntent)
        views.setOnClickPendingIntent(R.id.widgetMusicIcon, mainPendingIntent)
        views.setOnClickPendingIntent(R.id.widgetHeader, mainPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    override fun onEnabled(context: Context) {

        super.onEnabled(context)
    }
    
    override fun onDisabled(context: Context) {

        super.onDisabled(context)
        if (MusicPlayerService.isServiceRunning()) {
            val serviceIntent = Intent(context, MusicPlayerService::class.java).apply {
                action = MusicPlayerService.ACTION_STOP
            }
            context.startService(serviceIntent)
        }
    }
}