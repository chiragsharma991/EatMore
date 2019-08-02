package dk.eatmore.partner.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.gson.JsonObject
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.rest.ApiClient
import dk.eatmore.partner.rest.ApiInterface
import dk.eatmore.partner.storage.PreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FirebaseInstanceIDService : FirebaseInstanceIdService() {




    companion object {
        private val TAG = "MyFirebaseIIDService"
        var shallIsendToken : Boolean = false

    }
//fXuO0eIHFVk:APA91bH9sUqF1kmZZIyDa8u8WRQCLgLeSh2JNwUqs34YQC0BHa6MEmAEt1pe_c8nwYtYM_A-6mu6DbyfsJCa0Z-EPoSVVq9yxxMqEdMLdE6ylC1WBhV1p2bhzsgBBaplB9jXDdTAISwH

//fXuO0eIHFVk%3AAPA91bH9sUqF1kmZZIyDa8u8WRQCLgLeSh2JNwUqs34YQC0BHa6MEmAEt1pe_c8nwYtYM_A-6mu6DbyfsJCa0Z-EPoSVVq9yxxMqEdMLdE6ylC1WBhV1p2bhzsgBBaplB9jXDdTAISwH
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: " + refreshedToken!!)
        PreferenceUtil.putValue(PreferenceUtil.TOKEN,"android_"+refreshedToken)
        PreferenceUtil.save()
        shallIsendToken=true

    }


}