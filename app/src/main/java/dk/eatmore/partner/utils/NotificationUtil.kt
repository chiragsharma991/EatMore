package dk.eatmore.partner.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.fcm.FirebaseMessagingService
import dk.eatmore.partner.storage.PreferenceUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.util.*





object NotificationUtil{


    private fun getNotificationIcon(): Int {
        val whiteIcon = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
        return if (whiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }


    fun fireNotification(context: Context, title: String, message: String, channelId: String, channelName: String): Notification {


        val selectedRing = PreferenceUtil.getString(PreferenceUtil.NOTIFICATIONSELECTEDTONE,"track_two")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder: Notification.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            var notificationChannel: NotificationChannel? = notificationManager.getNotificationChannel(channelId)
            if(notificationChannel == null){
                Log.e("TAG","notificationChannel == null---")
                //  notificationManager.deleteNotificationChannel(channelId)
                notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                val audioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build()
                val uri : Uri
                if(PreferenceUtil.getString(PreferenceUtil.NOTIFICATIONSELECTEDTONE,"track_two") == "Default")
                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                else
                uri = Uri.parse("android.resource://${context.packageName}/raw/$selectedRing")
                Log.e("URI",""+"0->>"+uri+" = "+PreferenceUtil.getString(PreferenceUtil.NOTIFICATIONSELECTEDTONE,"Default") )
                notificationChannel.setSound(uri, audioAttributes)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationBuilder = Notification.Builder(context, channelId)
            //notificationBuilder.setSound(uri)


        } else {
            notificationBuilder = Notification.Builder(context)
            val uri : Uri
            if(PreferenceUtil.getString(PreferenceUtil.NOTIFICATIONSELECTEDTONE,"track_two") == "Default")
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            else
                uri = Uri.parse("android.resource://${context.packageName}/raw/$selectedRing")
            Log.e("URI",""+"0+>>"+uri )
            notificationBuilder.setSound(uri)
        }
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        notificationBuilder.setSmallIcon(getNotificationIcon())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        notificationBuilder.setColor(ContextCompat.getColor(context, R.color.theme_color))
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(message)
        notificationBuilder.setSmallIcon(R.mipmap.icon_app)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentIntent(pendingIntent)

        val notification = notificationBuilder.build()
        notificationManager.notify(Random().nextInt(9999), notification)
        return notification
    }


}