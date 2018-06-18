package dk.eatmore.partner.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
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



class FirebaseMessagingService : FirebaseMessagingService() {



    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
      //  if (remoteMessage!!.data.isNotEmpty()) {
          //  Log.e(TAG, ""+remoteMessage!!.data)
            //{full_name=Jhon Smith, user_id=75, id=165, type=card_favourite, title=BUSINESS CARD, message=Jhon Smith added your card as favourite}
            //generateNotification(applicationContext, "" + remoteMessage!!.data["message"])
      //  }
        generateNotification(applicationContext, remoteMessage!!.notification!!.body!!)
        val intent = Intent(OrderInfoFragment.SWIPE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }




    companion object {
        private val TAG = "MyFirebaseMsgService"
        var CHANEL_ID = "EatMore360"
        var CHANEL_NAME = "EatMoreChanel"
        var CHANEL_DESC = "EatMore Desc"


        @SuppressLint("WrongConstant")
        fun generateNotification(context: Context, body: String) {
            var intent = Intent(context, MainActivity::class.java)
            //intent.putExtra(AppUtils.EXTRA_IS_FROM_NOTIFICATION, true)
            // intent.putExtra(Constants.EXTRA_NOTIFICATION_TYPE, map["type"])
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            var pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

            var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            var mBuilder = NotificationCompat.Builder(context, CHANEL_ID)
                    .setContentTitle(R.string.app_name.toString())
                    .setContentText(body)
                  //  .setStyle(NotificationCompat.BigTextStyle()
                     //       .bigText(map["message"]))
                    .setPriority(if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) NotificationManager.IMPORTANCE_HIGH else Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setSound(uri)
                    .setSmallIcon(getNotificationIcon())
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setAutoCancel(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var channel = NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                channel.description = CHANEL_DESC
                var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            var notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
        }

        private fun getNotificationIcon(): Int {
            val whiteIcon = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
            return if (whiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
        }
    }
}