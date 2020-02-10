package com.example.contactmanager


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class BaseApp: Application() {

    companion object {
        const val SHARED_PREFERENCES = "ContactsMVVMSharedPreferences"
        const val NOTIFICATION_PREFERENCE = "ContactsMVVMNotifications"
        const val THEME_PREFERENCE = "ContactsMVVMTheme"
        const val NOTIFICATION_CHANNEL_1 = "ContactsMVVMChannel1"
        const val NOTIFICATION_CHANNEL_2 = "ContactsMVVMChannel2"
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //from oreo on, we need channels
            val channel1 = NotificationChannel(
                NOTIFICATION_CHANNEL_1,
                "channel 1",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel1.description = "Notification Channel One"
            val channel2 = NotificationChannel(
                NOTIFICATION_CHANNEL_2,
                "channel 2",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel2.description = "Notification Channel Two"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
        }
    }
}