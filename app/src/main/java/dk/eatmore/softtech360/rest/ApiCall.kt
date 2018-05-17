package dk.eatmore.softtech360.rest

import android.util.Log
import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.Order
import retrofit2.Call
import java.net.URLEncoder
import kotlin.math.log


class ApiCall {
   // username,password_hash,type,login_type

    companion object {

        private fun getApiInterface(): ApiInterface {
            return ApiClient.getClient()!!.create(ApiInterface::class.java)
        }

        fun login(username : String, password_hash : String, type : String,login_type : String) : Call<JsonObject> {
            return getApiInterface().setLogin(username, password_hash, type,login_type)
        }

        fun myOrder(to : String, from : String, r_key : String,r_token : String) : Call<Order> {

            Log.e("Encode ",r_key)
            return getApiInterface().myOrder( to,from,"fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812","w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ07042017114812")
        }

    }




}