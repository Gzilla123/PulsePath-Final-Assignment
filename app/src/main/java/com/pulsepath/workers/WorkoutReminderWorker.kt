package com.pulsepath.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.pulsepath.R
import java.util.concurrent.TimeUnit

class WorkoutReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        createChannel()
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle(applicationContext.getString(R.string.reminder_title))
            .setContentText(applicationContext.getString(R.string.reminder_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIF_ID, notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Workout Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "workout_reminders"
        const val NOTIF_ID = 1001

        fun scheduleDailyReminder(context: Context) {
            val request = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(8, TimeUnit.HOURS)
                .addTag("daily_reminder")
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("daily_reminder", ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("daily_reminder")
        }
    }
}


class WorkoutForegroundService : android.app.Service() {
    override fun onBind(intent: android.content.Intent?) = null
    override fun onStartCommand(i: android.content.Intent?, f: Int, id: Int) = START_NOT_STICKY
}
