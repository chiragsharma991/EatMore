package dk.eatmore.softtech360.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.gson.JsonObject
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.rest.ApiInterface
import dk.eatmore.softtech360.storage.PreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FirebaseInstanceIDService : FirebaseInstanceIdService() {




    companion object {
        private val TAG = "MyFirebaseIIDService"
        var shallIsendToken : Boolean = false

    }



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
        shallIsendToken=true

    }


}