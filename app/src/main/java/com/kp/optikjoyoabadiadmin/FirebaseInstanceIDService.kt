package com.kp.optikjoyoabadiadmin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kp.optikjoyoabadiadmin.ui.activity.MainActivity

class FirebaseInstanceIDService: FirebaseMessagingService() {
    companion object {
        private val TAG = FirebaseInstanceIDService::class.java.simpleName
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(TAG, "Refreshed token: $s")
        //send to firebase firestore in user
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            sendNotification(it)
        }
    }

    /*private fun logToken(s: String, id: String){
        val userToken = hashMapOf(
            "uid" to id,
            "notifyToken" to s
        )
        fireDb.collection("Users")
            .add(userToken)
    }*/

    private fun sendNotification(remoteMessage: RemoteMessage.Notification) {
        val channelId = "101"
        val channelName = "Notifikesyen"
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.title)
            .setContentText(remoteMessage.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationBuilder.setChannelId(channelId)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = notificationBuilder.build()
        mNotificationManager.notify(0, notification)
    }
}