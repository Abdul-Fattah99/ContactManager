package com.example.contactmanager.broadcastrecievers


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.contactmanager.BaseApp
import com.example.contactmanager.R
import com.example.contactmanager.data.entities.Contact
import java.util.*

class DateChangedBroadcastReceiver(contactList: List<Contact> = emptyList()): BroadcastReceiver() {
    private val contacts = contactList

    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPrefs = context?.getSharedPreferences(BaseApp.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPrefs != null && sharedPrefs.getBoolean(BaseApp.NOTIFICATION_PREFERENCE, true)) {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Intent.ACTION_DATE_CHANGED == (intent!!.action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendNotificationChannelOne(context, contacts, notificationManager)
                } else {
                    sendNotificationLegacy(context, contacts, notificationManager)
                }
            }
        }
    }

    private fun sendNotificationChannelOne(context: Context, contacts: List<Contact>, manager: NotificationManagerCompat) {
        if (contacts.count() > 0) {
            val title: String = when(contacts.count()) {
                1 -> context.resources.getString(R.string.notification_title_single)
                else -> context.resources.getString(R.string.notification_title_multi)
            }

            val todayCal = Calendar.getInstance()
            todayCal.timeInMillis = System.currentTimeMillis()

            var count = 0;
            var message = "Make sure to wish ";
            contacts.forEach {
                if (it.birthday != null) {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = it.birthday!!.time

                    if (cal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)) {
                        count++
                        message += when (count) {
                            1 -> getFirstAvailableName(it)
                            else -> ", ${getFirstAvailableName(it)}"
                        }
                    }
                }
            }
            message += " a happy birthday today!"

            if (count > 0) {
                val notification = NotificationCompat.Builder(context, BaseApp.NOTIFICATION_CHANNEL_1)
                    .setSmallIcon(R.drawable.ic_cake_small)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                    .build()
                manager.notify(1, notification)
            }
        }
    }

    private fun sendNotificationChannelTwo(context: Context, contacts: List<Contact>, manager: NotificationManagerCompat) {
        if (contacts.count() > 0) {
            val title: String = when(contacts.count()) {
                1 -> context.resources.getString(R.string.notification_title_single)
                else -> context.resources.getString(R.string.notification_title_multi)
            }

            val todayCal = Calendar.getInstance()
            todayCal.timeInMillis = System.currentTimeMillis()

            var count = 0;
            var message = "Happy birthday to: ";
            contacts.forEach {
                if (it.birthday != null) {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = it.birthday!!.time

                    if (cal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)) {
                        count++
                        message += when (count) {
                            1 -> getFirstAvailableName(it)
                            else -> ", ${getFirstAvailableName(it)}"
                        }
                    }
                }
            }

            if (count > 0) {
                val notification = NotificationCompat.Builder(context, BaseApp.NOTIFICATION_CHANNEL_1)
                    .setSmallIcon(R.drawable.ic_cake_small)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                    .build()
                manager.notify(2, notification)
            }
        }
    }

    private fun sendNotificationLegacy(context: Context, contacts: List<Contact>, manager: NotificationManagerCompat) {
        if (contacts.count() > 0) {
            val title: String = when(contacts.count()) {
                1 -> context.resources.getString(R.string.notification_title_single)
                else -> context.resources.getString(R.string.notification_title_multi)
            }

            val todayCal = Calendar.getInstance()
            todayCal.timeInMillis = System.currentTimeMillis()

            var count = 0;
            var message = "Happy birthday to: ";
            contacts.forEach {
                if (it.birthday != null) {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = it.birthday!!.time

                    if (cal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)) {
                        count++
                        message += when (count) {
                            1 -> getFirstAvailableName(it)
                            else -> ", ${getFirstAvailableName(it)}"
                        }
                    }
                }
            }

            if (count > 0) {
                val notification = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_cake_small)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                    .build()
                manager.notify(3, notification)
            }
        }
    }

    private fun getFirstAvailableName(contact: Contact): String? {
        var identifier: String? = null
        if (contact.first_name != "" && contact.first_name != null) {
            identifier = contact.first_name.toString()
        } else if (contact.last_name != "" && contact.last_name != null) {
            identifier = contact.last_name.toString();
        } else if (contact.email != "" && contact.email != null) {
            identifier = contact.email.toString()
        }
        return identifier
    }
}