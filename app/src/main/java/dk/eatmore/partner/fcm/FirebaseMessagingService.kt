package dk.eatmore.partner.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.fragment.order.OrderInfoFragment
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.storage.PreferenceUtil
import android.media.AudioAttributes
import android.os.Environment
import dk.eatmore.partner.utils.BackgroundTaskHelper
import dk.eatmore.partner.utils.NotificationUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class FirebaseMessagingService : FirebaseMessagingService() {


    companion object {
        private val TAG = "MyFirebaseMsgService"
        var CHANEL_ID = "OrderDelivery01"
        var CHANEL_NAME = "OrderDeliveryNotification"
        var CHANEL_DESC = "Order Delivery information"
        val NOTIFICATION_PATH = "/Android/data/dk.eatmore.partner/sounds/"}


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Log.e("TAG", "remoteMessage----"+remoteMessage!!.data.toString())

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.e("TAG", "Message data payload: " + remoteMessage.data)
            NotificationUtil.fireNotification(context = applicationContext,channelId = CHANEL_ID,channelName = CHANEL_NAME,
                    title =remoteMessage.data.get("title").toString() ,
                    message = remoteMessage.data.get("body").toString())
            val intent = Intent(OrderInfoFragment.SWIPE)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        }

        // Check if message contains a notification payload.
 /*       if (remoteMessage.notification != null) {
            Log.e("TAG", "Message Notification Body: " + remoteMessage.notification!!.body!!)
            NotificationUtil.fireNotification(context = applicationContext,channelId = CHANEL_ID,channelName = CHANEL_NAME,
                    title =remoteMessage.notification!!.title!! ,
                    message = remoteMessage.notification!!.body!!)
            val intent = Intent(OrderInfoFragment.SWIPE)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }*/


    }


}