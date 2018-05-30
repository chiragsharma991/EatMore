package dk.eatmore.softtech360.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.gson.JsonObject
import dk.eatmore.softtech360.dashboard.fragment.order.r_key
import dk.eatmore.softtech360.dashboard.fragment.order.r_token
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.rest.ApiInterface
import dk.eatmore.softtech360.storage.PreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: " + refreshedToken!!)
        PreferenceUtil.putValue(PreferenceUtil.TOKEN, refreshedToken)
        PreferenceUtil.save()

        if(PreferenceUtil.getString(PreferenceUtil.USER_NAME,"") != ""){

            val r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
            val r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
            val user_id = PreferenceUtil.getString(PreferenceUtil.USER_ID, "")!!
            val apiService = ApiClient.getClient()!!.create(ApiInterface::class.java)

            val call = apiService.sendFcmToken(r_token = r_token,r_key = r_key,token = refreshedToken,device_type = "POS",user_id = user_id)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.e(TAG,response.toString())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString())
                }
            })
        }
    }

    companion object {
        private val TAG = "MyFirebaseIIDService"
    }
}