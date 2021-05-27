package com.raindragonn.chapter06_dust.appwidget

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.raindragonn.chapter06_dust.R
import com.raindragonn.chapter06_dust.data.Repository
import com.raindragonn.chapter06_dust.data.model.airquality.Grade
import kotlinx.coroutines.launch
import java.lang.Exception

// Created by raindragonn on 2021/05/27.

class SimpleAirQualityWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        context?.let {
            ContextCompat.startForegroundService(
                it,
                Intent(it, UpdateWidgetService::class.java)
            )
        }
    }

    class UpdateWidgetService : LifecycleService() {
        companion object {
            private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH_CHANNEL_ID"
            private const val NOTIFICATION_ID = 1011
        }

        override fun onCreate() {
            super.onCreate()

            createChannelIfNeeded()
            startForeground(
                NOTIFICATION_ID,
                createNotification()
            )
        }

        private fun createChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            "위젯 갱신 채널",
                            NotificationManager.IMPORTANCE_LOW
                        )
                    )
            }
        }

        private fun createNotification(): Notification =
            NotificationCompat.Builder(this, WIDGET_REFRESH_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_refresh)
                .build()

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val updateViews = RemoteViews(packageName, R.layout.widget_simple).apply {
                    setTextViewText(R.id.tv_result, "권한 없음")

                    setViewVisibility(R.id.tv_label, View.GONE)
                    setViewVisibility(R.id.tv_grade_label, View.GONE)

                }
                updateWidget(updateViews)
                stopSelf()
                return super.onStartCommand(intent, flags, startId)
            }

            LocationServices.getFusedLocationProviderClient(this)
                .lastLocation
                .addOnSuccessListener { location ->
                    lifecycleScope.launch {
                        try {
                            val monitoringStation = Repository.getNearByMonitoringStation(
                                longitude = location.longitude,
                                latitude = location.latitude
                            )
                            val airQuality = Repository.getLatestAirQualityData(
                                monitoringStation!!.stationName!!
                            )
                            val updateViews =
                                RemoteViews(packageName, R.layout.widget_simple).apply {
                                    setViewVisibility(R.id.tv_label, View.VISIBLE)
                                    setViewVisibility(R.id.tv_grade_label, View.VISIBLE)

                                    val currentGrade = (airQuality?.khaiGrade) ?: Grade.UNKWON
                                    setTextViewText(R.id.tv_result, currentGrade.emoji)
                                    setTextViewText(R.id.tv_grade_label, currentGrade.label)
                                }

                            updateWidget(updateViews)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            stopSelf()
                        }
                    }
                }

            return super.onStartCommand(intent, flags, startId)
        }

        private fun updateWidget(updateViews: RemoteViews) {
            val widgetProvider = ComponentName(this, SimpleAirQualityWidgetProvider::class.java)

            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews)
        }

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(true)
        }
    }
}