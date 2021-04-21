package ru.mrrobot1413.movieapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.mrrobot1413.movieapp.ui.MainActivity

class NotifyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val NAME = "title"
        const val BODY = "body"
        const val ICON = "icon"
        const val ID = "id"
        const val GRAPH = "graph"
        const val DESTINATION = "destination"
        const val CHANNEL_ID = "0"
        const val CHANNEL_NAME = "Reminder to watch a movie"
        const val NOTIFY_ID = 1
    }

    override fun doWork(): Result {
        sendNotification(
            inputData.getString(NAME),
            inputData.getString(BODY),
            inputData.getInt(ICON, 0),
            inputData.getInt(GRAPH, 0),
            inputData.getInt(DESTINATION, 0),
            inputData.getInt(ID, 0)
        )
        return Result.success()
    }

    private fun sendNotification(
        title: String?,
        message: String?,
        icon: Int,
        graph: Int,
        destination: Int,
        id: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val bundle = Bundle()
        bundle.putInt(MainActivity.MOVIE, id)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(graph)
            .setDestination(destination)
            .setArguments(bundle)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSmallIcon(icon)
            .setAutoCancel(true)
        notificationManager.notify(NOTIFY_ID, notification.build())
    }
}