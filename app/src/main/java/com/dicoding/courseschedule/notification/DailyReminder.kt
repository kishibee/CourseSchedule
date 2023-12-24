package com.dicoding.courseschedule.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.home.HomeActivity
import com.dicoding.courseschedule.util.ID_REPEATING
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_ID
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_NAME
import com.dicoding.courseschedule.util.NOTIFICATION_ID
import com.dicoding.courseschedule.util.executeThread

class DailyReminder : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val repository = DataRepository.getInstance(context)
            val courses = repository?.getTodaySchedule()

            courses?.let {
                if (it.isNotEmpty()) showNotification(context, it)
            }
        }
    }

    //TODO 12 : Implement daily reminder for every 06.00 a.m using AlarmManager
    @RequiresApi(Build.VERSION_CODES.N)
    fun setDailyReminder(context: Context) {
        val managerAlarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminder::class.java)
        val intentPending = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_IMMUTABLE)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        managerAlarm.setRepeating(
            AlarmManager.RTC_WAKEUP,
            AlarmManager.INTERVAL_DAY,
            calendar.timeInMillis,
            intentPending
        )
    }

    fun cancelAlarm(context: Context) {
        val managerAlarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminder::class.java)
        val intentPending = PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_IMMUTABLE)
        managerAlarm.cancel(intentPending)

    }

    private fun showNotification(context: Context, content: List<Course>) {
        //TODO 13 : Show today schedules in inbox style notification & open HomeActivity when notification tapped
        val notificationStyle = NotificationCompat.InboxStyle()
        val timeString = context.resources.getString(R.string.notification_message_format)
        content.forEach {
            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
            notificationStyle.addLine(courseData)
        }
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val intentPending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.getString(R.string.today_schedule))
            .setContentText(context.getString(R.string.notification_message_format))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intentPending)
            .setStyle(notificationStyle)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notif = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notif)
    }
}