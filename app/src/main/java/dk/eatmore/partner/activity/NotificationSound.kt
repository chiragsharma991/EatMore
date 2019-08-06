package dk.eatmore.partner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import dk.eatmore.partner.utils.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar.*
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import dk.eatmore.partner.R
import kotlinx.android.synthetic.main.notificationsound.*
import java.lang.reflect.Array.getLength
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import dk.eatmore.partner.storage.PreferenceUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_record_of_today.*
import kotlinx.android.synthetic.main.row_notificationsound.*
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import android.media.Ringtone
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import dk.eatmore.partner.dashboard.fragment.order.OrderDetails
import dk.eatmore.partner.fcm.FirebaseInstanceIDService
import dk.eatmore.partner.fcm.FirebaseMessagingService
import dk.eatmore.partner.rest.ApiClient
import dk.eatmore.partner.rest.ApiInterface
import dk.eatmore.partner.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationSound : BaseActivity() {


    companion object {

        val TAG = "NotificationSound"
        fun newInstance(): NotificationSound {
            return NotificationSound()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notificationsound)
        init(savedInstanceState)

    }

    val list : ArrayList<NotificationModel> = ArrayList()
    var mAdapter : NotificationSoundAdapter? = null
    var count : Int =0


    fun init(savedInstancedState: Bundle?) {
        initToolbar()
        list.clear()
        list.add(NotificationModel(ringtoneTitle = "Default" , isSelected = false,itemName ="Default"))
        list.add(NotificationModel(ringtoneTitle = "track_one" , isSelected = false,itemName ="Ringtone01"))
        list.add(NotificationModel(ringtoneTitle = "track_two" , isSelected = false,itemName ="Ringtone02"))
        list.add(NotificationModel(ringtoneTitle = "track_three" , isSelected = false,itemName ="Ringtone03"))
        list.add(NotificationModel(ringtoneTitle = "track_four" , isSelected = false,itemName ="Ringtone04"))
        list.add(NotificationModel(ringtoneTitle = "track_five" , isSelected = false,itemName ="Ringtone05"))

        mAdapter = NotificationSoundAdapter(list,object : NotificationSoundAdapter.AdapterListener{
            override fun itemClicked(position: Int) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.deleteNotificationChannel(FirebaseMessagingService.CHANEL_ID)
                    val count = PreferenceUtil.getInt(PreferenceUtil.COUNT,0) + 1
                    FirebaseMessagingService.CHANEL_ID= "OrderDelivery"+count
                    PreferenceUtil.putValue(PreferenceUtil.COUNT,count)
                    PreferenceUtil.save()
                }

                if(position == 0){
                    // default tone
                    val path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    playAssetSound(path)

                }else{
                    if(list[position].isSelected == false){
                        val path = Uri.parse("android.resource://$packageName/raw/${list[position].ringtoneTitle}")
                        playAssetSound(path)
                    }
                }

                for (notificationmodel in list){
                    notificationmodel.isSelected = false
                }
                list[position].isSelected=true
                PreferenceUtil.putValue(PreferenceUtil.NOTIFICATIONSELECTEDTONE,list[position].ringtoneTitle)
                PreferenceUtil.save()
                sendfcmtocken()
                mAdapter!!.notifyDataSetChanged()


            }
        })
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = mAdapter


    }


    private var mediaPlayer: MediaPlayer? =null

    fun playAssetSound(path: Uri) {
        try {

            if(mediaPlayer != null && mediaPlayer!!.isPlaying){
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            }
                // null then initialise
                mediaPlayer = MediaPlayer.create(this,path)
                mediaPlayer!!.start()
              //  mediaPlayer!!.setOnCompletionListener { it.release() }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun initToolbar() {

        img_toolbar_back.setImageResource(R.drawable.ic_back)
        txt_toolbar.text = getString(R.string.notification_sound)
        img_toolbar_back.setOnClickListener {
            finish()
        }

    }



    private fun sendfcmtocken() {

        val user_id = PreferenceUtil.getString(PreferenceUtil.USER_ID, "")!!
        val refreshedToken = PreferenceUtil.getString(PreferenceUtil.TOKEN, "")!!
        val apiService = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        val r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!

        val call = apiService.sendFcmToken(
                r_token = r_token,
                r_key = r_key,token = refreshedToken,
                device_type = "POS",
                user_id = user_id,
                app = Constants.RESTAURANT_APP_ANDROID,
                sound = PreferenceUtil.getString(PreferenceUtil.NOTIFICATIONSELECTEDTONE,"track_two")!!

        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                log("response.body----",response.body().toString())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })
    }

    override fun onDestroy() {

        try {

            if(mediaPlayer != null && mediaPlayer!!.isPlaying){
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }


}


class NotificationSoundAdapter(val list : ArrayList<NotificationModel> , val callback : AdapterListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_notificationsound, parent, false)
        val vh = MyViewHolder(itemView)

        return vh
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.init(list[position])
            holder.item.setOnClickListener {
                callback.itemClicked(position)
            }
        }
    }


    private class MyViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun init(notificationmodel : NotificationModel ){
            title.text = notificationmodel.itemName
            check.isChecked = if(PreferenceUtil.getString(PreferenceUtil.NOTIFICATIONSELECTEDTONE,"track_two") == notificationmodel.ringtoneTitle) true else false
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }


    interface AdapterListener {
        fun itemClicked(position: Int)
    }

}


data class NotificationModel (
        val ringtoneTitle : String , // this is name in raw folder
        var isSelected : Boolean,
        val itemName : String // this is for only display
)








